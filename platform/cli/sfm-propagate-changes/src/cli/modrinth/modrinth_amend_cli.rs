use super::modrinth_cli::ANSI_BOLD_BLUE;
use super::modrinth_cli::ANSI_BOLD_CYAN;
use super::modrinth_cli::ANSI_BOLD_GREEN;
use super::modrinth_cli::ANSI_BOLD_MAGENTA;
use super::modrinth_cli::ANSI_BOLD_WHITE;
use super::modrinth_cli::ANSI_BOLD_YELLOW;
use super::modrinth_cli::ANSI_DIM;
use super::modrinth_cli::amend_version_changelog;
use super::modrinth_cli::build_http_client;
use super::modrinth_cli::compute_wrapped_release_changelog;
use super::modrinth_cli::fetch_project_versions;
use super::modrinth_cli::filter_release_jars_by_branch;
use super::modrinth_cli::find_latest_historical_version_for_mc;
use super::modrinth_cli::get_ordered_release_jars;
use super::modrinth_cli::parse_mc_version_from_jar_name;
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

/// Arguments for amending Modrinth changelogs for current release jars.
#[derive(Facet, Debug)]
pub struct ModrinthAmendArgs {
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

    /// Resolve remote target and print the amend plan without updating Modrinth.
    #[facet(default, args::named, rename = "dry-run")]
    pub dry_run: bool,
}

impl ModrinthAmendArgs {
    /// # Errors
    ///
    /// Returns an error if the remote targets cannot be resolved or amended.
    pub fn invoke(self) -> eyre::Result<()> {
        release_amend(
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
    reason = "amend flow is clearer when kept in release-operation order"
)]
fn release_amend(
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
    let wrapped_changelog = compute_wrapped_release_changelog(&repo_root, &mod_version)?;

    let token_value = if dry_run {
        None
    } else {
        Some(resolve_token(token, op_secret)?)
    };
    let client = build_http_client(token_value.as_deref())?;

    let all_jars = get_ordered_release_jars(&jar_dir, &mod_version)?;
    let branch_query = branch.into_query()?;
    let jars = filter_release_jars_by_branch(all_jars, &branch_query)?;
    let mut target_versions: Vec<(String, String)> = Vec::new();
    for jar in jars {
        let mc_version = parse_mc_version_from_jar_name(&jar)?;
        let jar_name = jar
            .file_name()
            .and_then(OsStr::to_str)
            .map(ToString::to_string)
            .ok_or_else(|| eyre::eyre!("Invalid jar filename: {}", jar.display()))?;

        if target_versions
            .iter()
            .all(|(existing_mc, _)| existing_mc != &mc_version)
        {
            target_versions.push((mc_version, jar_name));
        }
    }

    let versions = fetch_project_versions(&client, &project_id)?;
    let mut amend_targets: Vec<(String, String, String)> = Vec::new();
    for (mc_version, jar_name) in target_versions {
        let version = find_latest_historical_version_for_mc(&versions, &mc_version)?;
        let historical_version_number =
            version.version_number.as_deref().unwrap_or_default().trim();
        if historical_version_number != mod_version {
            eyre::bail!(
                "Refusing to amend Modrinth version for MC {} because the latest remote version was {}, expected {} (version id {}).",
                mc_version,
                if historical_version_number.is_empty() {
                    "<missing>"
                } else {
                    historical_version_number
                },
                mod_version,
                version.id
            );
        }
        amend_targets.push((mc_version, version.id.clone(), jar_name));
    }

    info!("{} {}", style("Project ID:", ANSI_BOLD_CYAN), project_id);
    info!("{} {}", style("Mod version:", ANSI_BOLD_CYAN), mod_version);
    if dry_run {
        info!(
            "{} {}",
            style("Mode:", ANSI_BOLD_YELLOW),
            style("dry-run (no Modrinth mutations)", ANSI_BOLD_YELLOW)
        );
    }
    info!("{}", style("Amend targets:", ANSI_BOLD_CYAN));
    for (mc_version, version_id, jar_name) in &amend_targets {
        info!(
            "  {} {} {} {} {} {}",
            style("MC", ANSI_DIM),
            style(mc_version, ANSI_BOLD_BLUE),
            style("version id", ANSI_DIM),
            version_id,
            style("name", ANSI_DIM),
            jar_name
        );
    }

    if dry_run {
        info!(
            "{}",
            style(
                "Dry-run complete: remote Modrinth targets resolved; no versions amended.",
                ANSI_BOLD_GREEN
            )
        );
        return Ok(());
    }

    let prompt = format!(
        "{} {}",
        style(
            "Proceed to amend changelog on these versions?",
            ANSI_BOLD_YELLOW
        ),
        style("(y/N)", ANSI_BOLD_YELLOW)
    );
    if !prompt_yes_no(&prompt)? {
        info!("{}", style("Aborted release amend.", ANSI_BOLD_YELLOW));
        return Ok(());
    }

    for (mc_version, version_id, jar_name) in &amend_targets {
        info!(
            "{} {} {} {} {} {}",
            style("Amending version", ANSI_BOLD_WHITE),
            style(version_id, ANSI_BOLD_MAGENTA),
            style("for MC", ANSI_DIM),
            style(mc_version, ANSI_BOLD_BLUE),
            style("as", ANSI_DIM),
            jar_name
        );

        amend_version_changelog(&client, version_id, &wrapped_changelog)?;
    }

    info!(
        "{}",
        style(
            "Modrinth release changelog amend complete.",
            ANSI_BOLD_GREEN
        )
    );

    Ok(())
}
