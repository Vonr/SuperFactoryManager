use crate::cli::jar::get_jar_dir;
use crate::terminal_output::stdout_line;
use std::ffi::OsStr;

pub(super) fn invoke() -> eyre::Result<()> { // todo(2026-06-16) cli args struct problem
    let jar_dir = get_jar_dir()?;
    let jars = super::jar_dir_command::list_jar_files_sorted(&jar_dir)?;

    if jars.is_empty() {
        return stdout_line(format!("No jars found in {}", jar_dir.display()));
    }

    for jar in jars {
        if let Some(name) = jar.file_name().and_then(OsStr::to_str) {
            stdout_line(name)?;
        } else {
            stdout_line(jar.display())?;
        }
    }

    Ok(())
}
