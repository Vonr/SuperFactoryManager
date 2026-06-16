use super::modrinth_cli::ANSI_BOLD_BLUE;
use super::modrinth_cli::ANSI_BOLD_CYAN;
use super::modrinth_cli::ANSI_BOLD_GREEN;
use super::modrinth_cli::ANSI_BOLD_MAGENTA;
use super::modrinth_cli::ANSI_BOLD_WHITE;
use super::modrinth_cli::ANSI_BOLD_YELLOW;
use super::modrinth_cli::ANSI_DIM;
use super::modrinth_cli::build_http_client;
use super::modrinth_cli::build_release_plans;
use super::modrinth_cli::compute_wrapped_release_changelog;
use super::modrinth_cli::create_project_version;
use super::modrinth_cli::filter_release_jars_by_branch;
use super::modrinth_cli::get_ordered_release_jars;
use super::modrinth_cli::modrinth_versions_url;
use super::modrinth_cli::prompt_yes_no;
use super::modrinth_cli::read_mod_version;
use super::modrinth_cli::resolve_project_id;
use super::modrinth_cli::resolve_token;
use super::modrinth_cli::style;
use crate::cli::jar::BranchSelector;
use crate::cli::jar::get_jar_dir;
use crate::cli::repo_root::get_repo_root;
use facet::Facet;
use figue as args;
use std::ffi::OsStr;
use tracing::info;

/// Arguments for creating new Modrinth versions for each release jar.
#[derive(Facet, Debug)]
pub struct ModrinthNowArgs {
    /// Branch selector used to choose release jar Minecraft versions. Defaults to `core`.
    #[facet(default, args::named)]
    pub branch: BranchSelector,

    /// Modrinth project id/slug (defaults to Super Factory Manager).
    #[facet(default, args::named)]
    pub project: Option<String>,

    /// Modrinth API token; if omitted, `MODRINTH_TOKEN` is used, then 1Password lookup.
    #[facet(default, args::named)]
    pub token: Option<String>,

    /// 1Password secret reference used when token is omitted.
    #[facet(default, args::named, rename = "op-secret")]
    pub op_secret: Option<String>,

    /// Print planned uploads and metadata without uploading.
    #[facet(default, args::named, rename = "dry-run")]
    pub dry_run: bool,
}

impl ModrinthNowArgs {
    /// # Errors
    ///
    /// Returns an error if release metadata cannot be computed or uploaded.
    pub fn invoke(self) -> eyre::Result<()> {
        release_now(
            self.branch,
            self.project,
            self.token,
            self.op_secret,
            self.dry_run,
        )
    }
}

#[expect(
    clippy::too_many_lines,
    reason = "release flow is intentionally linear"
)]
fn release_now(
    branch: BranchSelector,
    project: Option<String>,
    token: Option<String>,
    op_secret: Option<String>,
    dry_run: bool,
) -> eyre::Result<()> {
    let project_id = resolve_project_id(project)?;

    let repo_root = get_repo_root()?;
    let gradle_properties = repo_root.join("platform/minecraft/gradle.properties");
    let jar_dir = get_jar_dir()?;

    let mod_version = read_mod_version(&gradle_properties)?;
    let changelog_section = compute_wrapped_release_changelog(&repo_root, &mod_version)?;
    let all_jars = get_ordered_release_jars(&jar_dir, &mod_version)?;
    let branch_query = branch.into_query()?;
    let jars = filter_release_jars_by_branch(all_jars, &branch_query)?;
    let plans = build_release_plans(&jars, &mod_version)?;

    info!("{} {}", style("Project ID:", ANSI_BOLD_CYAN), project_id);
    info!("{} {}", style("Mod version:", ANSI_BOLD_CYAN), mod_version);
    info!(
        "{} {}",
        style("Jar dir:", ANSI_BOLD_CYAN),
        jar_dir.display()
    );
    if dry_run {
        info!(
            "{} {}",
            style("Mode:", ANSI_BOLD_YELLOW),
            style("dry-run (no uploads)", ANSI_BOLD_YELLOW)
        );
    }
    info!("{}", style("Changelog:", ANSI_BOLD_CYAN));
    info!("{changelog_section}");

    info!("{}", style("Metadata preflight:", ANSI_BOLD_CYAN));
    for plan in &plans {
        info!(
            "  {} {} {} {} {} {}",
            style("MC", ANSI_DIM),
            style(&plan.mc_version, ANSI_BOLD_BLUE),
            style("=>", ANSI_DIM),
            style("loaders", ANSI_DIM),
            plan.loaders.join(", "),
            style(
                &format!("(game versions: {})", plan.game_versions.join(", ")),
                ANSI_DIM
            )
        );
    }

    let action = if dry_run {
        "run the dry-run release preview"
    } else {
        "create Modrinth versions"
    };

    let prompt = format!(
        "{} {} {}",
        style("Proceed to", ANSI_BOLD_YELLOW),
        style(action, ANSI_BOLD_YELLOW),
        style("? (y/N)", ANSI_BOLD_YELLOW)
    );
    if !prompt_yes_no(&prompt)? {
        info!("{}", style("Aborted release-now.", ANSI_BOLD_YELLOW));
        info!("{}", modrinth_versions_url(&project_id));
        return Ok(());
    }

    let client = if dry_run {
        None
    } else {
        let token_value = resolve_token(token, op_secret)?;
        Some(build_http_client(Some(&token_value))?)
    };

    for plan in plans {
        let jar_name = plan
            .jar_path
            .file_name()
            .and_then(OsStr::to_str)
            .map(ToString::to_string)
            .ok_or_else(|| eyre::eyre!("Invalid jar filename: {}", plan.jar_path.display()))?;

        info!(
            "{} {} {} {}",
            style("Uploading", ANSI_BOLD_WHITE),
            style(&jar_name, ANSI_BOLD_MAGENTA),
            style("for MC", ANSI_DIM),
            style(&plan.mc_version, ANSI_BOLD_BLUE)
        );

        if dry_run {
            info!(
                "  {} {}",
                style("metadata:", ANSI_DIM),
                style(
                    &format!(
                        "version_number={}, loaders=[{}], game_versions=[{}]",
                        plan.version_number,
                        plan.loaders.join(", "),
                        plan.game_versions.join(", ")
                    ),
                    ANSI_DIM
                )
            );
            continue;
        }

        let upload_id = create_project_version(
            client
                .as_ref()
                .ok_or_else(|| eyre::eyre!("internal error: missing Modrinth client for upload"))?,
            &project_id,
            &plan,
            &changelog_section,
        )?;

        info!(
            "  {} {}",
            style("created version id", ANSI_BOLD_GREEN),
            style(&upload_id, ANSI_BOLD_GREEN)
        );
    }

    if !dry_run {
        info!(
            "{}",
            style("Modrinth release upload complete.", ANSI_BOLD_GREEN)
        );
    }
    info!("{}", modrinth_versions_url(&project_id));

    Ok(())
}
