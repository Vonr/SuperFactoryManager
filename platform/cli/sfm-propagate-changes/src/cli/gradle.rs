use crate::cli::status::assert_worktrees_clean_or_autocommit_generated;
use crate::mc_version_filter::McVersionFilter;
use crate::paths::CACHE_DIR;
use crate::worktree::get_sorted_worktrees;
use chrono::Local;
use color_eyre::owo_colors::OwoColorize;
use eyre::Context;
use eyre::bail;
use facet::Facet;
use figue::{self as args};
use humansize::DECIMAL;
use humansize::format_size;
use std::fmt::Write as _;
use std::fs;
use std::io::Write as _;
use std::path::Path;
use std::path::PathBuf;
use std::process::ExitStatus;
use std::process::Stdio;
use std::time::Duration;
use std::time::Instant;
use tokio::fs::File;
use tokio::io::AsyncRead;
use tokio::io::AsyncReadExt;
use tokio::io::AsyncWriteExt;
use tokio::process::Command;
use tracing::debug;
use tracing::error;
use tracing::info;
use tracing::warn;

#[derive(Debug, Clone)]
struct TaskRun {
    task: GradleTask,
    state: TaskState,
}

#[derive(Debug, Clone)]
struct BranchRun {
    branch: String,
    tasks: Vec<TaskRun>,
}

#[derive(Debug, Clone)]
enum TaskState {
    Waiting,
    Running { start_time: Instant },
    Success { duration: Duration },
    Failed { duration: Duration },
    NotFound { reason: String },
    Skipped,
}

#[derive(Debug, Clone)]
enum GradleTask {
    RunData,
    RunGameTestServer,
    Other(String),
}

#[derive(Debug)]
struct TaskOutput {
    status: ExitStatus,
    stdout: String,
    stderr: String,
    duration: Duration,
    stdout_log_path: PathBuf,
    stderr_log_path: PathBuf,
}

#[derive(Debug)]
struct TaskError {
    message: String,
    output: Option<TaskOutput>,
    interrupted: bool,
}

impl GradleTask {
    fn from_input(input: &str) -> Self {
        match input.to_ascii_lowercase().as_str() {
            "rundata" => Self::RunData,
            "rungametestserver" | "rungametest" => Self::RunGameTestServer,
            _ => Self::Other(input.to_string()),
        }
    }

    fn as_gradle_arg(&self) -> &str {
        match self {
            Self::RunData => "runData",
            Self::RunGameTestServer => "runGameTestServer",
            Self::Other(task) => task,
        }
    }

    fn header_name(&self) -> String {
        self.as_gradle_arg().to_string()
    }

    fn needs_generated_preflight(&self) -> bool {
        matches!(self, Self::RunData)
    }

    fn is_success(&self, output: &TaskOutput) -> bool {
        let combined = format!("{}\n{}", output.stdout, output.stderr);

        match self {
            Self::RunData => {
                output.status.success()
                    || combined.contains("BUILD SUCCESSFUL")
                    || combined.contains("All providers took")
            }
            Self::RunGameTestServer => has_gametest_success(&combined),
            Self::Other(_) => output.status.success() || combined.contains("BUILD SUCCESSFUL"),
        }
    }
}

impl TaskState {
    fn plain_text(&self) -> String {
        match self {
            Self::Waiting => "waiting".to_string(),
            Self::Running { start_time } => {
                format!("running ({})", format_duration(start_time.elapsed()))
            }
            Self::Success { duration } => format_duration(*duration),
            Self::Failed { duration } => format!("failed ({})", format_duration(*duration)),
            Self::NotFound { reason } => format!("not found ({reason})"),
            Self::Skipped => "skipped".to_string(),
        }
    }

    fn colorized_text(&self) -> String {
        let text = self.plain_text();
        match self {
            Self::Waiting | Self::Skipped => text.dimmed().to_string(),
            Self::Running { .. } => text.yellow().bold().to_string(),
            Self::Success { .. } => text.green().bold().to_string(),
            Self::Failed { .. } => text.red().bold().to_string(),
            Self::NotFound { .. } => text.magenta().to_string(),
        }
    }
}

fn extract_failed_gametest_names(output: &str) -> Vec<String> {
    let mut names = Vec::new();
    let mut in_failed_section = false;

    for line in output.lines() {
        // Strip the log prefix, e.g. "[HH:MM:SS] [Server thread/INFO] [minecraft/GameTestServer]: "
        let content = if let Some(idx) = line.rfind("]: ") {
            &line[idx + 3..]
        } else {
            line
        };

        if content.contains("required tests failed :(") {
            in_failed_section = true;
            continue;
        }

        if in_failed_section {
            if content.contains("====") {
                break;
            }
            let stripped = content.trim();
            if let Some(name) = stripped.strip_prefix("- ") {
                names.push(name.trim().to_string());
            }
        }
    }

    names
}

fn print_gametest_failures(failures: &[(String, Vec<String>)]) {
    if failures.is_empty() {
        return;
    }
    println!();
    println!("{}", "FAILED GAME TESTS".red().bold());
    for (branch, names) in failures {
        for name in names {
            println!("  {}", format!("{branch}: {name}").red());
        }
    }
}

fn has_gametest_success(output: &str) -> bool {
    let prefix = "All ";
    let suffix = " required tests passed :)";

    for (start, _) in output.match_indices(prefix) {
        let after_prefix = &output[start + prefix.len()..];
        if let Some(end) = after_prefix.find(suffix) {
            let number = &after_prefix[..end];
            if !number.is_empty() && number.chars().all(|c| c.is_ascii_digit()) {
                return true;
            }
        }
    }

    false
}

fn format_duration(duration: Duration) -> String {
    let secs = duration.as_secs();
    if secs >= 60 {
        let mins = secs / 60;
        let remaining_secs = secs % 60;
        format!("{mins}m {remaining_secs:02}s")
    } else {
        format!("{}.{:01}s", secs, duration.subsec_millis() / 100)
    }
}

fn format_log_summary(label: &str, output: &str, log_path: &Path) -> String {
    let lines = output.lines().count();
    let bytes = fs::metadata(log_path)
        .map(|meta| meta.len())
        .unwrap_or_else(|_| u64::try_from(output.len()).unwrap_or(u64::MAX));

    format!("  {label}: {lines} lines ({})", format_size(bytes, DECIMAL))
}

fn format_report(branches: &[BranchRun], tasks: &[GradleTask]) -> String {
    let mut widths = Vec::with_capacity(tasks.len() + 1);
    widths.push(
        std::iter::once("mc version".len())
            .chain(branches.iter().map(|b| b.branch.len()))
            .max()
            .unwrap_or("mc version".len()),
    );

    for (task_index, task) in tasks.iter().enumerate() {
        let header = task.header_name();
        let max_cell = branches
            .iter()
            .filter_map(|b| b.tasks.get(task_index))
            .map(|t| t.state.plain_text().len())
            .max()
            .unwrap_or(0);
        widths.push(header.len().max(max_cell));
    }

    let mut out = String::new();
    let _ = writeln!(out, "{}", build_header_row(tasks, &widths).cyan().bold());
    for branch in branches {
        let _ = writeln!(out, "{}", build_branch_row(branch, &widths));
    }
    out
}

fn build_header_row(tasks: &[GradleTask], widths: &[usize]) -> String {
    let mut cells = Vec::with_capacity(tasks.len() + 1);
    cells.push(format!("{:width$}", "mc version", width = widths[0]));

    for (idx, task) in tasks.iter().enumerate() {
        cells.push(format!(
            "{:width$}",
            task.header_name(),
            width = widths[idx + 1]
        ));
    }

    cells.join(" | ")
}

fn build_branch_row(branch: &BranchRun, widths: &[usize]) -> String {
    let mut row = format!("{:width$}", branch.branch, width = widths[0]);

    for (idx, task) in branch.tasks.iter().enumerate() {
        row.push_str(" | ");

        let plain = task.state.plain_text();
        let styled = task.state.colorized_text();
        let padding = widths[idx + 1].saturating_sub(plain.len());

        row.push_str(&styled);
        if padding > 0 {
            row.push_str(&" ".repeat(padding));
        }
    }

    row
}

fn print_report_to_stderr(branches: &[BranchRun], tasks: &[GradleTask]) {
    eprintln!();
    eprintln!("{}", format_report(branches, tasks));
}

fn print_report_to_stdout(branches: &[BranchRun], tasks: &[GradleTask]) {
    println!();
    println!("{}", format_report(branches, tasks));
}

fn sanitize_for_path(input: &str) -> String {
    let sanitized: String = input
        .chars()
        .map(|c| {
            if c.is_ascii_alphanumeric() || c == '.' || c == '-' || c == '_' {
                c
            } else {
                '_'
            }
        })
        .collect();

    let trimmed = sanitized.trim_matches('_');
    if trimmed.is_empty() {
        "unnamed".to_string()
    } else {
        trimmed.to_string()
    }
}

fn create_gradle_run_log_dir(tasks: &[String]) -> eyre::Result<PathBuf> {
    let timestamp = Local::now().format("%Y-%m-%d_%H-%M-%S");
    let task_segment = tasks
        .iter()
        .map(|t| sanitize_for_path(t))
        .collect::<Vec<_>>()
        .join("+");
    let task_segment = if task_segment.len() > 80 {
        task_segment[..80].to_string()
    } else {
        task_segment
    };

    let run_dir_name = if task_segment.is_empty() {
        format!("gradle_{timestamp}")
    } else {
        format!("gradle_{timestamp}_{task_segment}")
    };

    let run_dir = CACHE_DIR.0.join("gradle-runs").join(run_dir_name);
    fs::create_dir_all(&run_dir).wrap_err_with(|| {
        format!(
            "Failed to create gradle run log directory: {}",
            run_dir.display()
        )
    })?;
    Ok(run_dir)
}

fn create_task_log_paths(
    run_log_dir: &Path,
    branch: &str,
    task_idx: usize,
    task: &GradleTask,
) -> eyre::Result<(PathBuf, PathBuf)> {
    let branch_dir = sanitize_for_path(branch);
    let task_dir = format!(
        "{:02}_{}",
        task_idx + 1,
        sanitize_for_path(task.as_gradle_arg())
    );

    let dir = run_log_dir.join(branch_dir).join(task_dir);
    fs::create_dir_all(&dir)
        .wrap_err_with(|| format!("Failed to create task log directory: {}", dir.display()))?;

    Ok((dir.join("stdout.log"), dir.join("stderr.log")))
}

async fn collect_output<R>(
    reader: R,
    stream_logs_to_console: bool,
    log_path: PathBuf,
) -> std::io::Result<String>
where
    R: AsyncRead + Unpin,
{
    let mut reader = reader;
    let mut log_file = File::create(log_path).await?;
    let mut buf = [0_u8; 8 * 1024];
    let mut out = Vec::new();

    loop {
        let bytes_read = reader.read(&mut buf).await?;
        if bytes_read == 0 {
            break;
        }

        let chunk = &buf[..bytes_read];
        if stream_logs_to_console {
            eprint!("{}", String::from_utf8_lossy(chunk));
            std::io::stderr().flush()?;
        }

        log_file.write_all(chunk).await?;
        log_file.flush().await?;
        out.extend_from_slice(chunk);
    }

    log_file.flush().await?;

    Ok(String::from_utf8_lossy(&out).into_owned())
}

#[expect(
    clippy::too_many_lines,
    reason = "Task execution, streaming, and Ctrl+C handling are clearer in one place."
)]
async fn run_gradle_task(
    gradlew: &Path,
    minecraft_dir: &Path,
    task: &GradleTask,
    show_logs: bool,
    stdout_log_path: &Path,
    stderr_log_path: &Path,
) -> Result<TaskOutput, TaskError> {
    debug!(
        path = %minecraft_dir.display(),
        task = %task.as_gradle_arg(),
        "Running task"
    );

    if show_logs {
        eprintln!(
            "{}",
            format!("━━━ {} ({})", task.as_gradle_arg(), minecraft_dir.display())
                .cyan()
                .bold()
        );
    }

    let start = Instant::now();
    let mut child = Command::new(gradlew)
        .arg("--console=plain")
        .arg(task.as_gradle_arg())
        .current_dir(minecraft_dir)
        .kill_on_drop(true)
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .spawn()
        .map_err(|err| TaskError {
            message: format!("Failed to start {}: {err}", task.as_gradle_arg()),
            output: None,
            interrupted: false,
        })?;

    let stdout = child.stdout.take().ok_or_else(|| TaskError {
        message: format!(
            "Failed to capture stdout for {} in {}",
            task.as_gradle_arg(),
            minecraft_dir.display()
        ),
        output: None,
        interrupted: false,
    })?;
    let stderr = child.stderr.take().ok_or_else(|| TaskError {
        message: format!(
            "Failed to capture stderr for {} in {}",
            task.as_gradle_arg(),
            minecraft_dir.display()
        ),
        output: None,
        interrupted: false,
    })?;

    let stdout_log_path_buf = stdout_log_path.to_path_buf();
    let stderr_log_path_buf = stderr_log_path.to_path_buf();
    let stdout_task = tokio::spawn(async move {
        collect_output(stdout, show_logs, stdout_log_path_buf).await
    });
    let stderr_task = tokio::spawn(async move {
        collect_output(stderr, show_logs, stderr_log_path_buf).await
    });

    let (status, interrupted) = tokio::select! {
        status_res = child.wait() => {
            let status = status_res.map_err(|err| TaskError {
                message: format!(
                    "Failed while waiting for {} in {}: {err}",
                    task.as_gradle_arg(),
                    minecraft_dir.display()
                ),
                output: None,
                interrupted: false,
            })?;
            (status, false)
        }
        signal_res = tokio::signal::ctrl_c() => {
            if let Err(err) = signal_res {
                warn!(
                    path = %minecraft_dir.display(),
                    task = %task.as_gradle_arg(),
                    "Failed to listen for Ctrl+C: {err}"
                );
            }
            warn!(
                path = %minecraft_dir.display(),
                task = %task.as_gradle_arg(),
                "Received Ctrl+C, terminating running gradle task"
            );
            let _ = child.kill().await;
            let status = child.wait().await.map_err(|err| TaskError {
                message: format!(
                    "Failed while terminating {} in {}: {err}",
                    task.as_gradle_arg(),
                    minecraft_dir.display()
                ),
                output: None,
                interrupted: false,
            })?;
            (status, true)
        }
    };

    let stdout = stdout_task
        .await
        .map_err(|err| TaskError {
            message: format!(
                "Failed while joining stdout reader for {} in {}: {err}",
                task.as_gradle_arg(),
                minecraft_dir.display()
            ),
            output: None,
            interrupted: false,
        })?
        .map_err(|err| TaskError {
            message: format!(
                "Failed while reading stdout for {} in {}: {err}",
                task.as_gradle_arg(),
                minecraft_dir.display()
            ),
            output: None,
            interrupted: false,
        })?;

    let stderr = stderr_task
        .await
        .map_err(|err| TaskError {
            message: format!(
                "Failed while joining stderr reader for {} in {}: {err}",
                task.as_gradle_arg(),
                minecraft_dir.display()
            ),
            output: None,
            interrupted: false,
        })?
        .map_err(|err| TaskError {
            message: format!(
                "Failed while reading stderr for {} in {}: {err}",
                task.as_gradle_arg(),
                minecraft_dir.display()
            ),
            output: None,
            interrupted: false,
        })?;

    let task_output = TaskOutput {
        status,
        stdout,
        stderr,
        duration: start.elapsed(),
        stdout_log_path: stdout_log_path.to_path_buf(),
        stderr_log_path: stderr_log_path.to_path_buf(),
    };

    if interrupted {
        return Err(TaskError {
            message: format!(
                "Interrupted by Ctrl+C while running {} in {}",
                task.as_gradle_arg(),
                minecraft_dir.display()
            ),
            output: Some(task_output),
            interrupted: true,
        });
    }

    if task.is_success(&task_output) {
        Ok(task_output)
    } else {
        Err(TaskError {
            message: format!(
                "{} failed for {} (exit: {:?})",
                task.as_gradle_arg(),
                minecraft_dir.display(),
                task_output.status.code()
            ),
            output: Some(task_output),
            interrupted: false,
        })
    }
}

/// Gradle command - runs arbitrary gradle tasks in each worktree in strict sequence.
#[derive(Facet, Debug, Default)]
pub struct GradleCommand {
    /// Gradle tasks to run (for example: `runData`, `runGameTestServer`, `test`).
    #[facet(args::positional)]
    pub tasks: Vec<String>,

    /// Minecraft version filter expression for branch names (examples: `>=1.21.0`, `<1.20`, `=1.20.4`).
    #[facet(default, args::named)]
    pub mc: Option<String>,

    /// If set, stream gradle stdout/stderr to the console while tasks run.
    ///
    /// By default logs are written to cache files only and not streamed.
    #[facet(rename = "show-logs", args::named, default = false)]
    pub show_logs: bool,

    /// If set, continue with later branches after a task failure.
    ///
    /// Remaining tasks for the failed branch are marked as skipped.
    #[facet(rename = "continue-on-error", args::named, default = false)]
    pub continue_on_error: bool,
}

impl GradleCommand {
    /// # Errors
    ///
    /// Returns an error if any task fails.
    pub fn invoke(self) -> eyre::Result<()> {
        let rt = tokio::runtime::Builder::new_multi_thread()
            .enable_all()
            .build()
            .wrap_err("Failed to create tokio runtime")?;

        rt.block_on(self.invoke_async())
    }

    #[expect(
        clippy::too_many_lines,
        reason = "Control flow and report updates are clearest when kept together."
    )]
    async fn invoke_async(self) -> eyre::Result<()> {
        if self.tasks.is_empty() {
            bail!("No tasks provided. Usage: sfm-propagate-changes gradle <task1> <task2> ...");
        }

        let mut worktrees = get_sorted_worktrees()?;
        let all_worktree_branches: Vec<String> =
            worktrees.iter().map(|wt| wt.branch.clone()).collect();
        let total_worktrees = worktrees.len();
        let mc_filter = self.mc.as_deref().map(McVersionFilter::parse).transpose()?;

        let mut excluded_worktree_branches = Vec::new();

        if let Some(filter) = mc_filter {
            worktrees.retain(|wt| match filter.matches_version_text(&wt.branch) {
                Some(true) => true,
                Some(false) => {
                    excluded_worktree_branches.push(wt.branch.clone());
                    debug!(branch = %wt.branch, filter = ?self.mc, "Skipping branch due to --mc filter");
                    false
                }
                None => {
                    excluded_worktree_branches.push(wt.branch.clone());
                    warn!(branch = %wt.branch, filter = ?self.mc, "Skipping non-version branch for --mc filter");
                    false
                }
            });
        }

        if worktrees.is_empty() {
            if self.mc.is_some() {
                println!("No worktrees match the requested --mc filter.");
            } else {
                println!("No worktrees found.");
            }
            return Ok(());
        }

        let tasks: Vec<GradleTask> = self
            .tasks
            .iter()
            .map(|task| GradleTask::from_input(task))
            .collect();

        let run_log_dir = create_gradle_run_log_dir(&self.tasks)?;
        println!("Gradle run logs: {}", run_log_dir.display());

        if tasks.iter().any(GradleTask::needs_generated_preflight) {
            assert_worktrees_clean_or_autocommit_generated(&worktrees)?;
        }

        let mut branches: Vec<BranchRun> = worktrees
            .iter()
            .map(|wt| BranchRun {
                branch: wt.branch.clone(),
                tasks: tasks
                    .iter()
                    .cloned()
                    .map(|task| TaskRun {
                        task,
                        state: TaskState::Waiting,
                    })
                    .collect(),
            })
            .collect();

        let included_worktree_branches: Vec<String> =
            worktrees.iter().map(|wt| wt.branch.clone()).collect();
        let worktrees_included = included_worktree_branches.len();
        let worktrees_excluded = total_worktrees.saturating_sub(worktrees.len());

        info!(
            tasks = ?self.tasks,
            mc_filter = ?self.mc,
            continue_on_error = self.continue_on_error,
            worktrees = ?all_worktree_branches,
            worktrees_included = ?included_worktree_branches,
            worktrees_included_count = worktrees_included,
            worktrees_total = total_worktrees,
            worktrees_excluded = ?excluded_worktree_branches,
            worktrees_excluded_count = worktrees_excluded,
            "Running gradle tasks in strict sequence"
        );

        let mut failures: Vec<String> = Vec::new();
        let mut gametest_failures: Vec<(String, Vec<String>)> = Vec::new();

        for (branch_idx, wt) in worktrees.iter().enumerate() {
            let minecraft_dir = wt.path.join("platform").join("minecraft");
            let gradlew = if cfg!(windows) {
                minecraft_dir.join("gradlew.bat")
            } else {
                minecraft_dir.join("gradlew")
            };

            if !minecraft_dir.exists() {
                let reason = "platform/minecraft not found".to_string();
                warn!(branch = %wt.branch, path = %wt.path.display(), "{reason}");
                for task in &mut branches[branch_idx].tasks {
                    task.state = TaskState::NotFound {
                        reason: reason.clone(),
                    };
                }
                print_report_to_stderr(&branches, &tasks);
                continue;
            }

            if !gradlew.exists() {
                let reason = format!("gradlew not found in {}", minecraft_dir.display());
                warn!(branch = %wt.branch, path = %minecraft_dir.display(), "{reason}");
                for task in &mut branches[branch_idx].tasks {
                    task.state = TaskState::NotFound {
                        reason: reason.clone(),
                    };
                }
                print_report_to_stderr(&branches, &tasks);
                continue;
            }

            for task_idx in 0..tasks.len() {
                branches[branch_idx].tasks[task_idx].state = TaskState::Running {
                    start_time: Instant::now(),
                };
                print_report_to_stderr(&branches, &tasks);

                let current_task = branches[branch_idx].tasks[task_idx].task.clone();
                debug!(
                    branch = %wt.branch,
                    path = %minecraft_dir.display(),
                    task = %current_task.as_gradle_arg(),
                    "Starting gradle task"
                );

                let result = {
                    let (stdout_log_path, stderr_log_path) =
                        create_task_log_paths(&run_log_dir, &wt.branch, task_idx, &current_task)?;

                    fs::write(&stdout_log_path, "").wrap_err_with(|| {
                        format!(
                            "Failed to initialize stdout log file: {}",
                            stdout_log_path.display()
                        )
                    })?;
                    fs::write(&stderr_log_path, "").wrap_err_with(|| {
                        format!(
                            "Failed to initialize stderr log file: {}",
                            stderr_log_path.display()
                        )
                    })?;

                    println!(
                        "Running {} for {}",
                        current_task.as_gradle_arg().cyan().bold(),
                        wt.branch.cyan().bold()
                    );
                    println!("  stdout: {}", stdout_log_path.display());
                    println!("  stderr: {}", stderr_log_path.display());

                    run_gradle_task(
                        &gradlew,
                        &minecraft_dir,
                        &current_task,
                        self.show_logs,
                        &stdout_log_path,
                        &stderr_log_path,
                    )
                    .await
                };

                match result {
                    Ok(output) => {
                        branches[branch_idx].tasks[task_idx].state = TaskState::Success {
                            duration: output.duration,
                        };
                        info!(
                            branch = %wt.branch,
                            task = %current_task.as_gradle_arg(),
                            duration = %format_duration(output.duration),
                            "Task succeeded"
                        );
                    }
                    Err(err) => {
                        let duration = err
                            .output
                            .as_ref()
                            .map_or_else(|| Duration::from_secs(0), |out| out.duration);
                        branches[branch_idx].tasks[task_idx].state = TaskState::Failed { duration };

                        for remaining in branches[branch_idx].tasks.iter_mut().skip(task_idx + 1) {
                            remaining.state = TaskState::Skipped;
                        }

                        if matches!(current_task, GradleTask::RunGameTestServer) {
                            if let Some(ref output) = err.output {
                                let combined = format!(
                                    "{}
{}",
                                    output.stdout, output.stderr
                                );
                                let names = extract_failed_gametest_names(&combined);
                                if !names.is_empty() {
                                    gametest_failures.push((wt.branch.clone(), names));
                                }
                            }
                        }

                        if let Some(ref output) = err.output {
                            println!(
                                "{}",
                                format_log_summary("stdout", &output.stdout, &output.stdout_log_path)
                            );
                            println!(
                                "{}",
                                format_log_summary("stderr", &output.stderr, &output.stderr_log_path)
                            );
                        }

                        error!(
                            branch = %wt.branch,
                            task = %current_task.as_gradle_arg(),
                            error = %err.message,
                            "Task failed"
                        );

                        failures.push(format!(
                            "branch: {}, task: {}, error: {}",
                            wt.branch,
                            current_task.as_gradle_arg(),
                            err.message
                        ));

                        if err.interrupted {
                            println!();
                            println!("{}", "ABORTED BY CTRL+C".yellow().bold());
                            println!(
                                "{}",
                                format!(
                                    "branch: {}, task: {}",
                                    wt.branch,
                                    current_task.as_gradle_arg()
                                )
                                .yellow()
                            );
                        }

                        if let Some(output) = err.output {
                            println!();
                            println!("{}", "FAILED COMMAND LOGS".red().bold());
                            println!(
                                "{}",
                                format!(
                                    "branch: {}, task: {}, exit: {:?}",
                                    wt.branch,
                                    current_task.as_gradle_arg(),
                                    output.status.code()
                                )
                                .red()
                            );
                        }

                        if self.continue_on_error && !err.interrupted {
                            print_report_to_stderr(&branches, &tasks);
                            continue;
                        }

                        for later_branch in branches.iter_mut().skip(branch_idx + 1) {
                            for task in &mut later_branch.tasks {
                                task.state = TaskState::Skipped;
                            }
                        }
                        print_gametest_failures(&gametest_failures);
                        print_report_to_stdout(&branches, &tasks);
                        bail!(err.message);
                    }
                }
            }
        }

        print_gametest_failures(&gametest_failures);
        print_report_to_stdout(&branches, &tasks);

        if !failures.is_empty() {
            let mut summary = String::from("One or more gradle tasks failed:\n");
            for failure in &failures {
                let _ = writeln!(summary, "  - {failure}");
            }
            bail!(summary);
        }

        Ok(())
    }
}
