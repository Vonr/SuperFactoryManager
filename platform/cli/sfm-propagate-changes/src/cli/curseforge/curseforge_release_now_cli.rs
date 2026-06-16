#![allow(clippy::doc_markdown)]

use super::curseforge_cli::ANSI_BOLD_BLUE;
use super::curseforge_cli::ANSI_BOLD_CYAN;
use super::curseforge_cli::ANSI_BOLD_GREEN;
use super::curseforge_cli::ANSI_BOLD_MAGENTA;
use super::curseforge_cli::ANSI_BOLD_WHITE;
use super::curseforge_cli::ANSI_BOLD_YELLOW;
use super::curseforge_cli::ANSI_DIM;
use super::curseforge_cli::amend_file_changelog;
use super::curseforge_cli::build_http_client;
use super::curseforge_cli::build_upload_plans;
use super::curseforge_cli::colorize_metadata_name;
use super::curseforge_cli::curseforge_files_url;
use super::curseforge_cli::fetch_game_versions;
use super::curseforge_cli::filter_release_jars_by_branch;
use super::curseforge_cli::game_version_names_for_release;
use super::curseforge_cli::get_ordered_release_jars;
use super::curseforge_cli::parse_mc_version_from_jar_name;
use super::curseforge_cli::prompt_yes_no;
use super::curseforge_cli::read_changelog_section;
use super::curseforge_cli::read_mod_version;
use super::curseforge_cli::resolve_project_id;
use super::curseforge_cli::resolve_token;
use super::curseforge_cli::style;
use super::curseforge_cli::upload_project_file;
use crate::cli::jar::BranchSelector;
use crate::cli::jar::get_jar_dir;
use crate::cli::repo_root::get_repo_root;
use crate::curseforge::CurseforgeGameVersionId;
use facet::Facet;
use figue as args;
use std::ffi::OsStr;
use tracing::info;

/// Arguments for uploading release jars to CurseForge.
#[derive(Facet, Debug)]
pub struct CurseforgeReleaseNowArgs {
    /// Branch selector expression. Defaults to core worktrees.
    #[facet(default, args::named)]
    pub branch: BranchSelector,

    /// CurseForge project ID (defaults to configured default project).
    #[facet(default, args::named)]
    pub project: Option<u64>,

    /// CurseForge API token; if omitted, CURSEFORGE_API_TOKEN is used, then 1Password lookup.
    #[facet(default, args::named)]
    pub token: Option<String>,

    /// 1Password secret reference used for Core API key lookup.
    #[facet(default, args::named, rename = "op-secret")]
    pub op_secret: Option<String>,

    /// Print planned uploads and metadata without uploading.
    #[facet(default, args::named, rename = "dry-run")]
    pub dry_run: bool,
}

impl CurseforgeReleaseNowArgs {
    /// # Errors
    ///
    /// Returns an error if release jars cannot be uploaded.
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
    project: Option<u64>,
    token: Option<String>,
    op_secret: Option<String>,
    dry_run: bool,
) -> eyre::Result<()> {
    let branch_query = branch.into_query()?;
    let project_id = resolve_project_id(project)?;

    let repo_root = get_repo_root()?;
    let gradle_properties = repo_root.join("platform/minecraft/gradle.properties");
    let changelog_path = repo_root
        .join("platform/minecraft/src/main/resources/assets/sfm/template_programs/changelog.sfml");
    let jar_dir = get_jar_dir()?;

    let mod_version = read_mod_version(&gradle_properties)?;
    let changelog_section = read_changelog_section(&changelog_path, &mod_version)?;
    let wrapped_changelog = format!("```\n{}\n```", changelog_section.trim());
    let all_jars = get_ordered_release_jars(&jar_dir, &mod_version)?;
    let jars = filter_release_jars_by_branch(all_jars, &branch_query)?;

    let game_version_index = if dry_run {
        None
    } else {
        let token_value = resolve_token(token, op_secret)?;
        let client = build_http_client(&token_value)?;
        let game_versions = fetch_game_versions(&client)?;
        Some((client, CurseforgeGameVersionId::build_index(&game_versions)))
    };
    // todo(2026-06-16) I need to test this to see how badly our tracing subscriber mangles the presentation of this compared to our println version. might want to adapt this to be a writer or something
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
    info!("{wrapped_changelog}");

    let upload_plans = if dry_run {
        None
    } else {
        let (_, index) = game_version_index
            .as_ref()
            .ok_or_else(|| eyre::eyre!("internal error: missing game version index"))?;
        Some(build_upload_plans(
            &jars,
            &mod_version,
            &wrapped_changelog,
            index,
        )?)
    };

    if let Some(plans) = &upload_plans {
        info!("{}", style("Metadata preflight:", ANSI_BOLD_CYAN));
        for plan in plans {
            let metadata_pairs: Vec<String> = plan
                .metadata_names
                .iter()
                .zip(plan.metadata.game_versions.iter())
                .map(|(name, id)| format!("{name}:{id}"))
                .collect();
            info!(
                "  {} {} {} {}",
                style("MC", ANSI_DIM),
                style(&plan.mc_version, ANSI_BOLD_BLUE),
                style("=>", ANSI_DIM),
                metadata_pairs.join(", ")
            );
        }
    }

    let action = if dry_run {
        "run the dry-run release preview"
    } else {
        "upload files to CurseForge"
    };
    let prompt = format!(
        "{} {} {}",
        style("Proceed to", ANSI_BOLD_YELLOW),
        style(action, ANSI_BOLD_YELLOW),
        style("? (y/N)", ANSI_BOLD_YELLOW)
    );
    if !prompt_yes_no(&prompt)? {
        info!("{}", style("Aborted release-now.", ANSI_BOLD_YELLOW));
        info!("{}", curseforge_files_url(project_id));
        return Ok(());
    }

    for jar in jars {
        let mc_version = parse_mc_version_from_jar_name(&jar)?;

        let jar_name = jar
            .file_name()
            .and_then(OsStr::to_str)
            .map(ToString::to_string)
            .ok_or_else(|| eyre::eyre!("Invalid jar filename: {}", jar.display()))?;

        info!(
            "{} {} {} {}",
            style("Uploading", ANSI_BOLD_WHITE),
            style(&jar_name, ANSI_BOLD_MAGENTA),
            style("for MC", ANSI_DIM),
            style(&mc_version, ANSI_BOLD_BLUE)
        );
        if dry_run {
            let game_version_names = game_version_names_for_release(&mc_version)?;
            let colored_metadata_names: Vec<String> = game_version_names
                .iter()
                .map(|name| colorize_metadata_name(name, &mc_version))
                .collect();
            info!(
                "  {} {}",
                style("metadata names:", ANSI_DIM),
                colored_metadata_names.join(", ")
            );
            continue;
        }

        let plan = upload_plans
            .as_ref()
            .and_then(|plans| plans.iter().find(|plan| plan.jar_path == jar))
            .ok_or_else(|| {
                eyre::eyre!("internal error: missing upload plan for {}", jar.display())
            })?;

        let (client, _) = game_version_index.as_ref().ok_or_else(|| {
            eyre::eyre!("internal error: missing CurseForge client for non-dry-run upload")
        })?;

        let uploaded_id = upload_project_file(client, project_id, &jar, &plan.metadata)?;
        amend_file_changelog(
            client,
            project_id,
            uploaded_id,
            &plan.metadata.display_name,
            &plan.metadata.changelog,
        )?;
        info!(
            "  {} {}",
            style("uploaded file id", ANSI_BOLD_GREEN),
            style(&uploaded_id.to_string(), ANSI_BOLD_GREEN)
        );
    }

    if !dry_run {
        info!(
            "{}",
            style("CurseForge release upload complete.", ANSI_BOLD_GREEN)
        );
    }
    info!("{}", curseforge_files_url(project_id));

    Ok(())
}
