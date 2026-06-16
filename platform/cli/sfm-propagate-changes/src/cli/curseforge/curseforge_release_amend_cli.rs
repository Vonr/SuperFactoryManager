#![allow(clippy::doc_markdown)]

use super::curseforge_cli::ANSI_BOLD_BLUE;
use super::curseforge_cli::ANSI_BOLD_CYAN;
use super::curseforge_cli::ANSI_BOLD_GREEN;
use super::curseforge_cli::ANSI_BOLD_MAGENTA;
use super::curseforge_cli::ANSI_BOLD_WHITE;
use super::curseforge_cli::ANSI_BOLD_YELLOW;
use super::curseforge_cli::ANSI_DIM;
use super::curseforge_cli::DEFAULT_AMEND_SAFETY_AGE;
use super::curseforge_cli::amend_file_changelog;
use super::curseforge_cli::build_core_http_client;
use super::curseforge_cli::build_http_client;
use super::curseforge_cli::fetch_project_files;
use super::curseforge_cli::filter_release_jars_by_branch;
use super::curseforge_cli::find_latest_historical_file_for_mc;
use super::curseforge_cli::format_age;
use super::curseforge_cli::get_ordered_release_jars;
use super::curseforge_cli::historical_mod_version;
use super::curseforge_cli::parse_file_age;
use super::curseforge_cli::parse_mc_version_from_jar_name;
use super::curseforge_cli::parse_safety_age;
use super::curseforge_cli::prompt_yes_no;
use super::curseforge_cli::read_changelog_section;
use super::curseforge_cli::read_mod_version;
use super::curseforge_cli::resolve_core_api_key;
use super::curseforge_cli::resolve_project_id;
use super::curseforge_cli::resolve_token;
use super::curseforge_cli::style;
use crate::cli::jar::BranchSelector;
use crate::cli::jar::get_jar_dir;
use crate::cli::repo_root::get_repo_root;
use crate::curseforge::CurseforgeProjectFileId;
use chrono::Utc;
use facet::Facet;
use figue as args;
use std::ffi::OsStr;
use std::time::Duration;
use tracing::info;

/// Arguments for amending CurseForge changelogs for current release jars.
#[derive(Facet, Debug)]
pub struct CurseforgeReleaseAmendArgs {
    /// Branch selector expression. Defaults to core worktrees.
    #[facet(default, args::named)]
    pub branch: BranchSelector,

    /// CurseForge project ID (defaults to configured default project).
    #[facet(default, args::named)]
    pub project: Option<u64>,

    /// CurseForge Core API key; if omitted, CURSEFORGE_CORE_API_KEY is used.
    #[facet(default, args::named, rename = "api-key")]
    pub api_key: Option<String>,

    /// CurseForge API token; if omitted, CURSEFORGE_API_TOKEN is used, then 1Password lookup.
    #[facet(default, args::named)]
    pub token: Option<String>,

    /// 1Password secret reference used when credentials are omitted.
    #[facet(default, args::named, rename = "op-secret")]
    pub op_secret: Option<String>,

    /// Refuse amending files older than this age (examples: 30m, 2h, 45s).
    #[facet(default, args::named, rename = "safety-age")]
    pub safety_age: Option<String>,

    /// Resolve remote target and print the amend plan without updating CurseForge.
    #[facet(default, args::named, rename = "dry-run")]
    pub dry_run: bool,
}

impl CurseforgeReleaseAmendArgs {
    /// # Errors
    ///
    /// Returns an error if release files cannot be resolved or amended.
    pub fn invoke(self) -> eyre::Result<()> {
        release_amend(
            self.branch,
            self.project,
            self.api_key,
            self.token,
            self.op_secret,
            self.safety_age,
            self.dry_run,
        )
    }
}

#[expect(clippy::too_many_lines, reason = "amend flow is intentionally linear")]
fn release_amend(
    branch: BranchSelector,
    project: Option<u64>,
    api_key: Option<String>,
    token: Option<String>,
    op_secret: Option<String>,
    safety_age: Option<String>,
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

    let upload_client = if dry_run {
        None
    } else {
        let token_value = resolve_token(token.clone(), op_secret.clone())?;
        Some(build_http_client(&token_value)?)
    };

    let all_jars = get_ordered_release_jars(&jar_dir, &mod_version)?;
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

    let (core_key, credential_source) = resolve_core_api_key(api_key, token, op_secret)?;
    let client = build_core_http_client(&core_key)?;
    let files = fetch_project_files(&client, project_id, &credential_source)?;

    let safety_age_text = safety_age.unwrap_or_else(|| DEFAULT_AMEND_SAFETY_AGE.to_string());
    let max_file_age = parse_safety_age(&safety_age_text)?;
    let now = Utc::now();

    let mut file_targets: Vec<(String, CurseforgeProjectFileId, String, String, Duration)> =
        Vec::new();
    for (mc_version, jar_name) in target_versions {
        let file = find_latest_historical_file_for_mc(&files, &mc_version)?;
        let historical_version = historical_mod_version(file).ok_or_else(|| {
            eyre::eyre!(
                "Could not parse mod version from latest historical file {} for MC {}",
                file.id,
                mc_version
            )
        })?;
        if historical_version != mod_version {
            eyre::bail!(
                "Refusing to amend CurseForge file for MC {} because the latest remote version was {}, expected {} (file id {}).",
                mc_version,
                historical_version,
                mod_version,
                file.id
            );
        }
        let old_name = file
            .display_name
            .clone()
            .or_else(|| file.file_name.clone())
            .unwrap_or_else(|| "<unknown>".to_string());
        let file_age = parse_file_age(file.file_date.as_deref(), now)?;
        if file_age > max_file_age {
            eyre::bail!(
                "Refusing to amend file {} for MC {} because it is {} old (safety-age is {}).",
                file.id,
                mc_version,
                format_age(file_age),
                safety_age_text
            );
        }
        file_targets.push((mc_version, file.id, old_name, jar_name, file_age));
    }

    info!("{} {}", style("Project ID:", ANSI_BOLD_CYAN), project_id);
    info!("{} {}", style("Mod version:", ANSI_BOLD_CYAN), mod_version);
    info!(
        "{} {}",
        style("Safety age:", ANSI_BOLD_CYAN),
        style(&safety_age_text, ANSI_BOLD_CYAN)
    );
    if dry_run {
        info!(
            "{} {}",
            style("Mode:", ANSI_BOLD_YELLOW),
            style("dry-run (no CurseForge mutations)", ANSI_BOLD_YELLOW)
        );
    }
    info!("{}", style("Amend targets:", ANSI_BOLD_CYAN));
    for (mc_version, file_id, old_name, jar_name, file_age) in &file_targets {
        info!(
            "  {} {} {} {} {} {} {} {} {} {}",
            style("MC", ANSI_DIM),
            style(mc_version, ANSI_BOLD_BLUE),
            style("file id", ANSI_DIM),
            file_id,
            style("age", ANSI_DIM),
            style(&format_age(*file_age), ANSI_BOLD_YELLOW),
            style("old", ANSI_DIM),
            old_name,
            style("new", ANSI_DIM),
            jar_name
        );
    }

    if dry_run {
        info!(
            "{}",
            style(
                "Dry-run complete: remote CurseForge targets resolved; no files amended.",
                ANSI_BOLD_GREEN
            )
        );
        return Ok(());
    }

    let prompt = format!(
        "{} {}",
        style(
            "Proceed to amend changelog on these files?",
            ANSI_BOLD_YELLOW
        ),
        style("(y/N)", ANSI_BOLD_YELLOW)
    );
    if !prompt_yes_no(&prompt)? {
        info!("{}", style("Aborted release amend.", ANSI_BOLD_YELLOW));
        return Ok(());
    }

    for (mc_version, file_id, old_name, jar_name, file_age) in &file_targets {
        info!(
            "{} {} {} {} {} {} {} {} {} {}",
            style("Amending file", ANSI_BOLD_WHITE),
            style(&file_id.to_string(), ANSI_BOLD_MAGENTA),
            style("for MC", ANSI_DIM),
            style(mc_version, ANSI_BOLD_BLUE),
            style("age", ANSI_DIM),
            style(&format_age(*file_age), ANSI_BOLD_YELLOW),
            style("old", ANSI_DIM),
            old_name,
            style("new", ANSI_DIM),
            jar_name
        );
        let upload_client = upload_client.as_ref().ok_or_else(|| {
            eyre::eyre!("internal error: missing CurseForge upload client for amend")
        })?;
        amend_file_changelog(
            upload_client,
            project_id,
            *file_id,
            jar_name,
            &wrapped_changelog,
        )?;
    }

    info!(
        "{}",
        style(
            "CurseForge release changelog amend complete.",
            ANSI_BOLD_GREEN
        )
    );

    Ok(())
}
