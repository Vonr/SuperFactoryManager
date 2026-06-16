#![allow(clippy::doc_markdown)]

//! https://docs.curseforge.com/rest-api/#get-mod
//! https://support.curseforge.com/support/solutions/articles/9000197321-curseforge-api
//! https://www.curseforge.com/minecraft/mc-mods/super-factory-manager Project ID - 306935

use super::CurseforgeMinecraftArgs;
use super::CurseforgeMinecraftVersionArgs;
use super::CurseforgeMinecraftVersionListArgs;
use super::CurseforgeProjectArgs;
use super::CurseforgeProjectDefaultArgs;
use super::CurseforgeProjectDefaultSetArgs;
use super::CurseforgeProjectDefaultShowArgs;
use super::CurseforgeProjectFileArgs;
use super::CurseforgeProjectFileListArgs;
use super::CurseforgeReleaseAmendArgs;
use super::CurseforgeReleaseArgs;
use super::CurseforgeReleaseCheckArgs;
use super::CurseforgeReleaseNowArgs;
use super::CurseforgeReleaseValidateArgs;
use crate::branch_targets::BranchQuery;
use crate::branch_targets::select_required_minecraft_versions;
use crate::paths::APP_HOME;
use crate::terminal_output::stdout_prompt;
use crate::worktree::parse_version;
use chrono::DateTime;
use chrono::Utc;
use eyre::Context;
use facet::Facet;
use figue as args;
use reqwest::blocking::Client;
use reqwest::blocking::multipart;
use reqwest::header::HeaderMap;
use reqwest::header::HeaderValue;
use reqwest::header::USER_AGENT;
use sha1::Digest;
use sha1::Sha1;
use std::collections::BTreeSet;
use std::collections::HashMap;
use std::ffi::OsStr;
use std::fmt::Write as _;
use std::path::Path;
use std::path::PathBuf;
use std::process::Command;
use std::time::Duration;
use tracing::debug;

pub(super) const CURSEFORGE_API_ROOT: &str = "https://minecraft.curseforge.com/api";
pub(super) const CURSEFORGE_CORE_API_ROOT: &str = "https://api.curseforge.com/v1";
pub(super) const CURSEFORGE_DEFAULT_PROJECT_FILE: &str = "curseforge_project_id.txt";
pub(super) const CURSEFORGE_DEFAULT_PROJECT_ID: u64 = 306_935;
pub(super) const CURSEFORGE_TOKEN_ENV_VAR: &str = "CURSEFORGE_API_TOKEN";
pub(super) const CURSEFORGE_CORE_API_KEY_ENV_VAR: &str = "CURSEFORGE_CORE_API_KEY";
pub(super) const DEFAULT_OP_SECRET_REFERENCE: &str =
    "op://Private/CurseForge SFM Upload token/credential";
pub(super) const DEFAULT_OP_CORE_API_KEY_SECRET_REFERENCE: &str =
    "op://Private/SFM CurseForge studios token/credential";
pub(super) const DEFAULT_AMEND_SAFETY_AGE: &str = "30m";
pub(super) const CURSEFORGE_AUTHORS_FILES_URL_PREFIX: &str =
    "https://authors.curseforge.com/#/projects";
pub(super) type CurseforgeVersionRow = (u64, String, String, (u32, u32, u32));

pub(super) const ANSI_RESET: &str = "\x1b[0m";
pub(super) const ANSI_BOLD_CYAN: &str = "\x1b[1;36m";
pub(super) const ANSI_BOLD_YELLOW: &str = "\x1b[1;33m";
pub(super) const ANSI_BOLD_GREEN: &str = "\x1b[1;32m";
pub(super) const ANSI_BOLD_BLUE: &str = "\x1b[1;34m";
pub(super) const ANSI_BOLD_MAGENTA: &str = "\x1b[1;35m";
pub(super) const ANSI_BOLD_RED: &str = "\x1b[1;31m";
pub(super) const ANSI_BOLD_WHITE: &str = "\x1b[1;37m";
pub(super) const ANSI_DIM: &str = "\x1b[2m";

pub(super) fn style(text: &str, ansi: &str) -> String {
    format!("{ansi}{text}{ANSI_RESET}")
}

pub(super) fn prompt_yes_no(message: &str) -> eyre::Result<bool> {
    stdout_prompt(format!("{message} "))?;

    let mut input = String::new();
    std::io::stdin()
        .read_line(&mut input)
        .wrap_err("Failed to read confirmation response")?;

    let normalized = input.trim().to_ascii_lowercase();
    Ok(matches!(normalized.as_str(), "y" | "yes"))
}

pub(super) fn colorize_metadata_name(name: &str, mc_version: &str) -> String {
    // todo(2026-06-16) shouldn't we have an enum for the known variants with an Other(String) escape hatch where this fn would be an instance method?
    if name == mc_version {
        return style(name, ANSI_BOLD_BLUE);
    }

    match name {
        "NeoForge" => style(name, ANSI_BOLD_RED),
        "Forge" => style(name, ANSI_BOLD_YELLOW),
        "Java 17" => style(name, ANSI_BOLD_GREEN),
        "Java 21" | "Java 25" => style(name, ANSI_BOLD_CYAN),
        _ => name.to_string(),
    }
}

pub(super) fn curseforge_files_url(project_id: u64) -> String {
    format!("{CURSEFORGE_AUTHORS_FILES_URL_PREFIX}/{project_id}/files")
}

/// Arguments for CurseForge release and file related commands.
#[derive(Facet, Debug)]
pub struct CurseforgeArgs {
    /// CurseForge subcommand.
    #[facet(args::subcommand)]
    pub command: CurseforgeCommand,
}

impl CurseforgeArgs {
    /// # Errors
    ///
    /// Returns an error if the selected CurseForge command fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}

/// CurseForge release and file related commands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum CurseforgeCommand {
    /// Project-related operations
    Project(CurseforgeProjectArgs),
    /// Minecraft metadata operations
    Minecraft(CurseforgeMinecraftArgs),
    /// Release metadata validation and upload operations
    Release(CurseforgeReleaseArgs),
}

/// CurseForge release subcommands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum CurseforgeReleaseCommand {
    /// Verify computed release metadata against historical project uploads
    Check(CurseforgeReleaseCheckArgs),
    /// Validate remote downloadable files against local release jars by hash
    Validate(CurseforgeReleaseValidateArgs),
    /// Upload each release jar to CurseForge according to release-process rules
    Now(CurseforgeReleaseNowArgs),
    /// Amend changelog for latest file per MC version in current release jars
    Amend(CurseforgeReleaseAmendArgs),
}

/// CurseForge project subcommands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum CurseforgeProjectCommand {
    /// Project default configuration commands
    Default(CurseforgeProjectDefaultArgs),
    /// Project file operations
    File(CurseforgeProjectFileArgs),
}

/// CurseForge project default subcommands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum CurseforgeProjectDefaultCommand {
    /// Set default project ID
    Set(CurseforgeProjectDefaultSetArgs),
    /// Show default project ID (falls back to built-in default)
    Show(CurseforgeProjectDefaultShowArgs),
}

/// CurseForge project file subcommands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum CurseforgeProjectFileCommand {
    /// List files for a project
    List(CurseforgeProjectFileListArgs),
}

/// CurseForge minecraft subcommands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum CurseforgeMinecraftCommand {
    /// Minecraft version operations
    Version(CurseforgeMinecraftVersionArgs),
}

/// CurseForge minecraft version subcommands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum CurseforgeMinecraftVersionCommand {
    /// List Minecraft game versions from CurseForge
    List(CurseforgeMinecraftVersionListArgs),
}

impl CurseforgeCommand {
    /// # Errors
    ///
    /// This function will return an error if the subcommand fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            Self::Project(args) => args.invoke(),
            Self::Minecraft(args) => args.invoke(),
            Self::Release(args) => args.invoke(),
        }
    }
}

impl CurseforgeReleaseCommand {
    /// # Errors
    ///
    /// This function will return an error if the subcommand fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            Self::Check(args) => args.invoke(),
            Self::Validate(args) => args.invoke(),
            Self::Now(args) => args.invoke(),
            Self::Amend(args) => args.invoke(),
        }
    }
}

impl CurseforgeProjectCommand {
    /// # Errors
    ///
    /// This function will return an error if the subcommand fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            Self::Default(args) => args.invoke(),
            Self::File(args) => args.invoke(),
        }
    }
}

impl CurseforgeProjectDefaultCommand {
    /// # Errors
    ///
    /// This function will return an error if the subcommand fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            Self::Set(args) => args.invoke(),
            Self::Show(args) => args.invoke(),
        }
    }
}

impl CurseforgeProjectFileCommand {
    /// # Errors
    ///
    /// This function will return an error if the subcommand fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            Self::List(args) => args.invoke(),
        }
    }
}

#[derive(Facet, Debug, Clone)]
pub(super) struct CurseforgeProjectFileItem {
    pub(super) id: u64,
    #[facet(default, rename = "fileName")]
    pub(super) file_name: Option<String>,
    #[facet(default, rename = "displayName")]
    pub(super) display_name: Option<String>,
    #[facet(default, rename = "releaseType")]
    pub(super) release_type: Option<u32>,
    #[facet(default, rename = "fileStatus")]
    pub(super) file_status: Option<u32>,
    #[facet(default, rename = "gameVersions")]
    pub(super) game_versions: Vec<String>,
    #[facet(default, rename = "downloadUrl")]
    pub(super) download_url: Option<String>,
    #[facet(default, rename = "fileDate")]
    pub(super) file_date: Option<String>,
    #[facet(default)]
    pub(super) hashes: Vec<CurseforgeProjectFileHash>,
}

#[derive(Facet, Debug, Clone)]
pub(super) struct CurseforgeProjectFileHash {
    #[facet(default)]
    pub(super) algo: Option<u32>,
    #[facet(default)]
    pub(super) value: Option<String>,
}

#[derive(Facet, Debug, Clone)]
pub(super) struct CurseforgeProjectFileListEnvelope {
    #[facet(default)]
    pub(super) data: Vec<CurseforgeProjectFileItem>,
}

#[derive(Facet, Debug, Clone)]
pub(super) struct CurseforgeUploadResponse {
    pub(super) id: u64,
}

#[derive(Facet, Debug, Clone)]
pub(super) struct CurseforgeGameVersion {
    pub(super) id: u64,
    #[facet(default, rename = "gameVersionTypeID")]
    pub(super) game_version_type_id: Option<u64>,
    pub(super) name: String,
    #[facet(default)]
    pub(super) slug: Option<String>,
}

#[derive(Facet, Debug, Clone)]
pub(super) struct UploadMetadata {
    pub(super) changelog: String,
    #[facet(rename = "changelogType")]
    pub(super) changelog_type: String,
    #[facet(rename = "displayName")]
    pub(super) display_name: String,
    #[facet(rename = "gameVersions")]
    pub(super) game_versions: Vec<u64>,
    #[facet(rename = "releaseType")]
    pub(super) release_type: String,
}

#[derive(Facet, Debug, Clone)]
pub(super) struct CurseforgeAmendFilePayload {
    #[facet(rename = "fileID")]
    pub(super) file_id: u64,
    pub(super) changelog: String,
    #[facet(rename = "changelogType")]
    pub(super) changelog_type: String,
    #[facet(rename = "displayName")]
    pub(super) display_name: String,
}

pub(super) fn get_default_project_id() -> eyre::Result<u64> {
    let path = APP_HOME.file_path(CURSEFORGE_DEFAULT_PROJECT_FILE);
    if !path.exists() {
        return Ok(CURSEFORGE_DEFAULT_PROJECT_ID);
    }

    let content = std::fs::read_to_string(&path)
        .wrap_err_with(|| format!("Failed to read default project file: {}", path.display()))?;

    let trimmed = content.trim();
    if trimmed.is_empty() {
        return Ok(CURSEFORGE_DEFAULT_PROJECT_ID);
    }

    trimmed
        .parse::<u64>()
        .wrap_err_with(|| format!("Invalid project id in default project file: '{trimmed}'"))
}

pub(super) fn resolve_project_id(project: Option<u64>) -> eyre::Result<u64> {
    match project {
        Some(project_id) => Ok(project_id),
        None => get_default_project_id(),
    }
}

pub(super) fn resolve_token(
    token: Option<String>,
    op_secret: Option<String>,
) -> eyre::Result<String> {
    if let Some(value) = token {
        let trimmed = value.trim().to_string();
        if trimmed.is_empty() {
            eyre::bail!("Provided --token was empty");
        }
        return Ok(trimmed);
    }

    if let Ok(env_token) = std::env::var(CURSEFORGE_TOKEN_ENV_VAR) {
        let trimmed = env_token.trim().to_string();
        if !trimmed.is_empty() {
            return Ok(trimmed);
        }
    }

    let secret_reference = op_secret.unwrap_or_else(|| DEFAULT_OP_SECRET_REFERENCE.to_string());
    read_secret_from_1password(&secret_reference, "token")
}

pub(super) fn read_secret_from_1password(
    secret_reference: &str,
    what: &str,
) -> eyre::Result<String> {
    let output = Command::new("op")
        .args(["read", secret_reference, "--no-newline"])
        .output()
        .wrap_err("Failed to run 1Password CLI (`op`)")?;

    if !output.status.success() {
        eyre::bail!(
            "Failed to read {what} from 1Password secret '{}': {}",
            secret_reference,
            String::from_utf8_lossy(&output.stderr)
        );
    }

    let value = String::from_utf8_lossy(&output.stdout).trim().to_string();
    if value.is_empty() {
        eyre::bail!(
            "1Password returned an empty {what} for secret '{}'",
            secret_reference
        );
    }

    Ok(value)
}

pub(super) fn build_http_client(token: &str) -> eyre::Result<Client> {
    let mut headers = HeaderMap::new();
    headers.insert(
        "X-Api-Token",
        HeaderValue::from_str(token).wrap_err("Invalid API token for header")?,
    );
    headers.insert(
        USER_AGENT,
        HeaderValue::from_static("sfm-propagate-changes/curseforge"),
    );

    Client::builder()
        .default_headers(headers)
        .timeout(Duration::from_mins(2))
        .build()
        .wrap_err("Failed to build HTTP client")
}

pub(super) fn resolve_core_api_key(
    api_key: Option<String>,
    token: Option<String>,
    op_secret: Option<String>,
) -> eyre::Result<(String, String)> {
    if let Some(value) = api_key {
        let trimmed = value.trim().to_string();
        if trimmed.is_empty() {
            eyre::bail!("Provided --api-key was empty");
        }
        return Ok((trimmed, "--api-key".to_string()));
    }

    if let Ok(env_key) = std::env::var(CURSEFORGE_CORE_API_KEY_ENV_VAR) {
        let trimmed = env_key.trim().to_string();
        if !trimmed.is_empty() {
            return Ok((trimmed, CURSEFORGE_CORE_API_KEY_ENV_VAR.to_string()));
        }
    }

    if let Some(secret_reference) = op_secret {
        return read_secret_from_1password(&secret_reference, "Core API key")
            .map(|value| (value, format!("1Password ({secret_reference})")));
    }

    if let Ok(value) =
        read_secret_from_1password(DEFAULT_OP_CORE_API_KEY_SECRET_REFERENCE, "Core API key")
    {
        return Ok((
            value,
            format!("1Password ({DEFAULT_OP_CORE_API_KEY_SECRET_REFERENCE})"),
        ));
    }

    if let Ok(value) = read_secret_from_1password(DEFAULT_OP_SECRET_REFERENCE, "Core API key") {
        return Ok((value, format!("1Password ({DEFAULT_OP_SECRET_REFERENCE})")));
    }

    if let Some(value) = token {
        let trimmed = value.trim().to_string();
        if trimmed.is_empty() {
            eyre::bail!("Provided --token was empty");
        }
        return Ok((trimmed, "--token".to_string()));
    }

    if let Ok(env_token) = std::env::var(CURSEFORGE_TOKEN_ENV_VAR) {
        let trimmed = env_token.trim().to_string();
        if !trimmed.is_empty() {
            return Ok((trimmed, CURSEFORGE_TOKEN_ENV_VAR.to_string()));
        }
    }

    eyre::bail!(
        "Could not resolve CurseForge Core API key. Tried --api-key, {CURSEFORGE_CORE_API_KEY_ENV_VAR}, and 1Password defaults:\n\
         - {DEFAULT_OP_CORE_API_KEY_SECRET_REFERENCE}\n\
         - {DEFAULT_OP_SECRET_REFERENCE}"
    )
}

pub(super) fn build_core_http_client(api_key: &str) -> eyre::Result<Client> {
    let mut headers = HeaderMap::new();
    headers.insert(
        "x-api-key",
        HeaderValue::from_str(api_key).wrap_err("Invalid Core API key for header")?,
    );
    headers.insert(
        USER_AGENT,
        HeaderValue::from_static("sfm-propagate-changes/curseforge"),
    );

    Client::builder()
        .default_headers(headers)
        .timeout(Duration::from_mins(2))
        .build()
        .wrap_err("Failed to build Core API HTTP client")
}

pub(super) fn fetch_project_files(
    client: &Client,
    project_id: u64,
    credential_source: &str,
) -> eyre::Result<Vec<CurseforgeProjectFileItem>> {
    let url = format!("{CURSEFORGE_CORE_API_ROOT}/mods/{project_id}/files");
    let response = client
        .get(&url)
        .send()
        .wrap_err_with(|| format!("Failed to query project files: {url}"))?;

    let status = response.status();
    let body = response
        .text()
        .wrap_err("Failed to read project files response")?;
    debug!(body, url, ?status);

    if !status.is_success() {
        if status == reqwest::StatusCode::FORBIDDEN {
            eyre::bail!(
                "CurseForge project files API failed (403 Forbidden) using credential from: {credential_source}.\n\
                 Ensure this is a CurseForge Core API key (x-api-key), not the upload token.\n\
                 You can override with --api-key or --op-secret '<core-api-key-secret-ref>'."
            );
        }

        eyre::bail!("CurseForge project files API failed ({status}): {body}",);
    }

    if let Ok(items) = facet_json::from_str::<Vec<CurseforgeProjectFileItem>>(&body) {
        return Ok(items);
    }

    let envelope: CurseforgeProjectFileListEnvelope =
        facet_json::from_str(&body).wrap_err("Failed to parse project files response JSON")?;
    Ok(envelope.data)
}

#[derive(Debug, Clone)]
pub(super) struct UploadPlan {
    pub(super) jar_path: PathBuf,
    pub(super) mc_version: String,
    pub(super) metadata_names: Vec<String>,
    pub(super) metadata: UploadMetadata,
}

#[derive(Debug, Clone)]
pub(super) struct ResolvedMetadataPlan {
    pub(super) jar_path: PathBuf,
    pub(super) mc_version: String,
    pub(super) metadata_names: Vec<String>,
    pub(super) game_version_ids: Vec<u64>,
}

pub(super) fn build_resolved_metadata_plans(
    jars: &[PathBuf],
    game_version_index: &HashMap<String, Vec<CurseforgeGameVersion>>,
) -> eyre::Result<Vec<ResolvedMetadataPlan>> {
    let mut plans = Vec::with_capacity(jars.len());

    for jar in jars {
        let mc_version = parse_mc_version_from_jar_name(jar)?;
        let metadata_names = game_version_names_for_release(&mc_version)?;
        let game_version_ids = resolve_game_version_ids(game_version_index, &metadata_names)?;

        plans.push(ResolvedMetadataPlan {
            jar_path: jar.clone(),
            mc_version,
            metadata_names,
            game_version_ids,
        });
    }

    Ok(plans)
}

pub(super) fn build_upload_plans(
    jars: &[PathBuf],
    mod_version: &str,
    changelog_section: &str,
    game_version_index: &HashMap<String, Vec<CurseforgeGameVersion>>,
) -> eyre::Result<Vec<UploadPlan>> {
    let resolved = build_resolved_metadata_plans(jars, game_version_index)?;
    let mut plans = Vec::with_capacity(resolved.len());

    for plan in resolved {
        let mc_version = plan.mc_version;
        let metadata_names = plan.metadata_names;
        let game_version_ids = plan.game_version_ids;

        let display_name = format!("Super Factory Manager MC{mc_version} v{mod_version}");
        let metadata = UploadMetadata {
            changelog: changelog_section.to_string(),
            changelog_type: "markdown".to_string(),
            display_name,
            game_versions: game_version_ids,
            release_type: "release".to_string(),
        };

        plans.push(UploadPlan {
            jar_path: plan.jar_path,
            mc_version,
            metadata_names,
            metadata,
        });
    }

    Ok(plans)
}

pub(super) fn filter_release_jars_by_branch(
    jars: Vec<PathBuf>,
    branch_query: &BranchQuery,
) -> eyre::Result<Vec<PathBuf>> {
    let selected_versions = select_required_minecraft_versions(branch_query)?;
    let mut filtered = Vec::new();
    for jar in jars {
        let mc_version = parse_mc_version_from_jar_name(&jar)?; // todo(2026-06-16) should we have a SfmJarName newtype?
        if selected_versions
            .iter()
            .any(|selected| selected.as_str() == mc_version)
        {
            filtered.push(jar);
        }
    }

    if filtered.is_empty() {
        eyre::bail!("No release jars matched --branch '{branch_query}'.");
    }

    Ok(filtered)
}

pub(super) fn is_java_metadata_name(value: &str) -> bool {
    value.starts_with("Java ")
}

pub(super) fn to_comparison_name_set(values: &[String]) -> BTreeSet<String> {
    values
        .iter()
        .filter(|value| !is_java_metadata_name(value))
        .cloned()
        .collect()
}

pub(super) fn find_latest_historical_file_for_mc<'a>(
    files: &'a [CurseforgeProjectFileItem],
    mc_version: &str,
) -> eyre::Result<&'a CurseforgeProjectFileItem> {
    files
        .iter()
        .filter(|file| file.game_versions.iter().any(|value| value == mc_version))
        .max_by_key(|file| file.id)
        .ok_or_else(|| eyre::eyre!("No historical project file found for MC {}", mc_version))
}

pub(super) fn parse_mod_version_from_release_name(name: &str) -> Option<String> {
    let trimmed = name.trim();
    if let Some(without_jar) = trimmed.strip_suffix(".jar") {
        let version = without_jar.rsplit('-').next()?;
        if version
            .chars()
            .all(|character| character.is_ascii_digit() || character == '.')
        {
            return Some(version.to_string());
        }
    }

    if let Some((_, remainder)) = trimmed.rsplit_once('v') {
        let version = remainder.trim();
        if version
            .chars()
            .all(|character| character.is_ascii_digit() || character == '.')
        {
            return Some(version.to_string());
        }
    }

    None
}

pub(super) fn historical_mod_version(file: &CurseforgeProjectFileItem) -> Option<String> {
    file.file_name
        .as_deref()
        .and_then(parse_mod_version_from_release_name)
        .or_else(|| {
            file.display_name
                .as_deref()
                .and_then(parse_mod_version_from_release_name)
        })
}

pub(super) fn parse_safety_age(value: &str) -> eyre::Result<Duration> {
    let trimmed = value.trim();
    if trimmed.is_empty() {
        eyre::bail!("--safety-age cannot be empty");
    }

    let mut chars = trimmed.chars();
    let unit = chars
        .next_back()
        .ok_or_else(|| eyre::eyre!("Invalid --safety-age value: {trimmed}"))?;
    let amount_text = chars.as_str();
    let amount: u64 = amount_text
        .parse()
        .wrap_err_with(|| format!("Invalid --safety-age amount: {amount_text}"))?;

    let seconds = match unit {
        's' => amount,
        'm' => amount
            .checked_mul(60)
            .ok_or_else(|| eyre::eyre!("--safety-age is too large"))?,
        'h' => amount
            .checked_mul(60)
            .and_then(|mins| mins.checked_mul(60))
            .ok_or_else(|| eyre::eyre!("--safety-age is too large"))?,
        _ => eyre::bail!(
            "Invalid --safety-age unit '{unit}'. Supported units: s, m, h (example: 30m)."
        ),
    };

    Ok(Duration::from_secs(seconds))
}

pub(super) fn parse_file_age(
    file_date: Option<&str>,
    now: DateTime<Utc>,
) -> eyre::Result<Duration> {
    let Some(file_date) = file_date else {
        eyre::bail!("Cannot determine age for historical file: missing fileDate");
    };
    let parsed = DateTime::parse_from_rfc3339(file_date)
        .wrap_err_with(|| format!("Invalid CurseForge fileDate: {file_date}"))?
        .with_timezone(&Utc);
    let age = now.signed_duration_since(parsed);
    if age.num_seconds() <= 0 {
        return Ok(Duration::from_secs(0));
    }
    age.to_std()
        .wrap_err_with(|| format!("Invalid age computed from fileDate: {file_date}"))
}

pub(super) fn format_age(value: Duration) -> String {
    let seconds = value.as_secs();
    if seconds >= 3600 {
        format!("{}h{}m", seconds / 3600, (seconds % 3600) / 60)
    } else if seconds >= 60 {
        format!("{}m{}s", seconds / 60, seconds % 60)
    } else {
        format!("{seconds}s")
    }
}

pub(super) fn amend_file_changelog(
    client: &Client,
    project_id: u64,
    file_id: u64,
    display_name: &str,
    changelog: &str,
) -> eyre::Result<()> {
    let payload = CurseforgeAmendFilePayload {
        file_id,
        changelog: changelog.to_string(),
        changelog_type: "markdown".to_string(),
        display_name: display_name.to_string(),
    };
    let body = facet_json::to_string(&payload)
        .wrap_err("Failed to encode amend changelog payload JSON")?;

    let form = multipart::Form::new().text("metadata", body);

    let url = format!("{CURSEFORGE_API_ROOT}/projects/{project_id}/update-file");
    let response = client
        .post(&url)
        .multipart(form)
        .send()
        .wrap_err_with(|| format!("Failed to amend CurseForge file {file_id}"))?;

    let status = response.status();
    let response_body = response
        .text()
        .wrap_err("Failed to read amend file response body")?;

    if !status.is_success() {
        eyre::bail!(
            "CurseForge amend failed for file {} ({status}): {response_body}",
            file_id
        );
    }

    let _parsed: CurseforgeUploadResponse =
        facet_json::from_str(&response_body).wrap_err("Failed to parse amend response JSON")?;

    Ok(())
}

pub(super) fn upload_project_file(
    client: &Client,
    project_id: u64,
    jar_path: &Path,
    metadata: &UploadMetadata,
) -> eyre::Result<u64> {
    let metadata_json =
        facet_json::to_string(metadata).wrap_err("Failed to encode upload metadata JSON")?;

    let form = multipart::Form::new()
        .text("metadata", metadata_json)
        .file("file", jar_path)
        .wrap_err_with(|| {
            format!(
                "Failed to attach jar file to upload form: {}",
                jar_path.display()
            )
        })?;

    let url = format!("{CURSEFORGE_API_ROOT}/projects/{project_id}/upload-file");
    let response =
        client.post(&url).multipart(form).send().wrap_err_with(|| {
            format!("Failed to upload jar to CurseForge: {}", jar_path.display())
        })?;

    let status = response.status();
    let body = response
        .text()
        .wrap_err("Failed to read upload response from CurseForge")?;

    if !status.is_success() {
        eyre::bail!(
            "CurseForge upload failed for {} ({status}): {body}",
            jar_path.display()
        );
    }

    let parsed: CurseforgeUploadResponse =
        facet_json::from_str(&body).wrap_err("Failed to parse upload response JSON")?;
    Ok(parsed.id)
}

pub(super) fn read_mod_version(gradle_properties: &Path) -> eyre::Result<String> {
    let content = std::fs::read_to_string(gradle_properties)
        .wrap_err_with(|| format!("Failed to read {}", gradle_properties.display()))?;

    let mod_version = content
        .lines()
        .map(str::trim)
        .filter(|line| !line.is_empty())
        .filter(|line| !line.starts_with('#'))
        .find_map(|line| line.strip_prefix("mod_version=").map(str::trim))
        .ok_or_else(|| eyre::eyre!("mod_version not found in {}", gradle_properties.display()))?;

    if mod_version.is_empty() {
        eyre::bail!("mod_version was empty in {}", gradle_properties.display());
    }

    Ok(mod_version.to_string())
}

pub(super) fn read_changelog_section(
    changelog_path: &Path,
    mod_version: &str,
) -> eyre::Result<String> {
    let lines: Vec<String> = std::fs::read_to_string(changelog_path)
        .wrap_err_with(|| format!("Failed to read changelog: {}", changelog_path.display()))?
        .lines()
        .map(ToString::to_string)
        .collect();

    if lines.is_empty() {
        eyre::bail!("Changelog file was empty: {}", changelog_path.display());
    }

    let heading_prefix = format!("---- {mod_version} ");
    let start_index = lines
        .iter()
        .position(|line| line.trim().starts_with(&heading_prefix))
        .ok_or_else(|| {
            eyre::eyre!(
                "Could not find changelog heading for version {mod_version} in {}",
                changelog_path.display()
            )
        })?;

    let mut end_index = lines.len();
    for (index, line) in lines.iter().enumerate().skip(start_index + 1) {
        let trimmed = line.trim();
        if trimmed.starts_with("---- ") {
            let maybe_version = trimmed
                .trim_start_matches("---- ")
                .split(' ')
                .next()
                .unwrap_or_default();
            if parse_version(maybe_version).is_some() {
                end_index = index;
                break;
            }
        }
    }

    Ok(lines[0..end_index].join("\n").trim().to_string())
}

pub(super) fn get_ordered_release_jars(
    jar_dir: &Path,
    mod_version: &str,
) -> eyre::Result<Vec<PathBuf>> {
    if !jar_dir.is_dir() {
        eyre::bail!("Jar directory does not exist: {}", jar_dir.display());
    }

    let suffix = format!("-{mod_version}.jar");
    let mut entries: Vec<(PathBuf, (u32, u32, u32), String)> = std::fs::read_dir(jar_dir)?
        .filter_map(Result::ok)
        .map(|entry| entry.path())
        .filter(|path| {
            path.is_file()
                && path
                    .extension()
                    .is_some_and(|extension| extension == OsStr::new("jar"))
                && path
                    .file_name()
                    .and_then(OsStr::to_str)
                    .is_some_and(|name| name.ends_with(&suffix))
        })
        .map(|path| {
            let name = path
                .file_name()
                .and_then(OsStr::to_str)
                .map(ToString::to_string)
                .unwrap_or_default();
            let version = parse_mc_version_from_jar_name(&path)
                .ok()
                .and_then(|value| parse_version(&value))
                .unwrap_or((0, 0, 0));
            (path, version, name)
        })
        .collect();

    if entries.is_empty() {
        eyre::bail!(
            "No jar files found in {} for mod version {}",
            jar_dir.display(),
            mod_version
        );
    }

    entries.sort_by(|a, b| {
        let left = (a.1, &a.2);
        let right = (b.1, &b.2);
        left.cmp(&right)
    });

    Ok(entries.into_iter().map(|entry| entry.0).collect())
}

pub(super) fn parse_mc_version_from_jar_name(jar_path: &Path) -> eyre::Result<String> {
    let file_name = jar_path
        .file_name()
        .and_then(OsStr::to_str)
        .ok_or_else(|| eyre::eyre!("Invalid jar filename: {}", jar_path.display()))?;

    let marker = "-MC";
    let start = file_name
        .find(marker)
        .ok_or_else(|| eyre::eyre!("Could not find '-MC' marker in jar name: {file_name}"))?
        + marker.len();
    let remainder = &file_name[start..];
    let end = remainder.find('-').ok_or_else(|| {
        eyre::eyre!("Could not find end of MC version marker in jar name: {file_name}")
    })?;

    let version = &remainder[..end];
    if parse_version(version).is_none() {
        eyre::bail!("Unsupported MC version marker in jar name: {file_name}");
    }

    Ok(version.to_string())
}

pub(super) fn fetch_game_versions(client: &Client) -> eyre::Result<Vec<CurseforgeGameVersion>> {
    let url = format!("{CURSEFORGE_API_ROOT}/game/versions");
    let response = client
        .get(&url)
        .send()
        .wrap_err_with(|| format!("Failed to query game versions: {url}"))?;

    let status = response.status();
    let body = response
        .text()
        .wrap_err("Failed to read game versions response")?;

    debug!(body, url, ?status);

    if !status.is_success() {
        eyre::bail!("CurseForge game versions API failed ({status}): {body}");
    }

    facet_json::from_str(&body).wrap_err("Failed to parse game versions response JSON")
}

pub(super) fn find_sha1_hash(hashes: &[CurseforgeProjectFileHash]) -> Option<String> {
    hashes.iter().find_map(|hash| {
        let is_sha1 = hash.algo == Some(1);
        if !is_sha1 {
            return None;
        }

        hash.value
            .as_deref()
            .map(str::trim)
            .filter(|value| !value.is_empty())
            .map(str::to_ascii_lowercase)
    })
}

pub(super) fn download_sha1(client: &Client, url: &str) -> eyre::Result<String> {
    let response = client
        .get(url)
        .send()
        .wrap_err_with(|| format!("Failed to download file from {url}"))?;

    let status = response.status();
    if !status.is_success() {
        let body = response
            .text()
            .unwrap_or_else(|_| "<failed to read response body>".to_string());
        eyre::bail!("Failed to download file from {} ({status}): {}", url, body);
    }

    let bytes = response
        .bytes()
        .wrap_err_with(|| format!("Failed to read downloaded bytes from {url}"))?;
    Ok(sha1_hex(bytes.as_ref()))
}

pub(super) fn sha1_hex(bytes: &[u8]) -> String {
    let mut hasher = Sha1::new();
    hasher.update(bytes);
    let digest = hasher.finalize();
    let mut output = String::with_capacity(digest.len() * 2);
    for byte in digest {
        let _ = write!(output, "{byte:02x}");
    }
    output
}

pub(super) fn build_game_version_index(
    game_versions: &[CurseforgeGameVersion],
) -> HashMap<String, Vec<CurseforgeGameVersion>> {
    let mut map: HashMap<String, Vec<CurseforgeGameVersion>> =
        HashMap::with_capacity(game_versions.len());
    for version in game_versions {
        let name_key = normalize_version_key(&version.name);
        map.entry(name_key).or_default().push(version.clone());
    }
    map
}

pub(super) fn format_candidates(candidates: &[CurseforgeGameVersion]) -> String {
    candidates
        .iter()
        .map(|candidate| {
            let type_id = candidate
                .game_version_type_id
                .map_or_else(|| "<none>".to_string(), |id| id.to_string());
            format!(
                "id={},type={},slug={}",
                candidate.id,
                type_id,
                candidate.slug.clone().unwrap_or_default()
            )
        })
        .collect::<Vec<_>>()
        .join("; ")
}

pub(super) fn resolve_exact_type_id(
    game_version_index: &HashMap<String, Vec<CurseforgeGameVersion>>,
    name: &str,
    required_type_id: u64,
) -> eyre::Result<u64> {
    let key = normalize_version_key(name);
    let candidates = game_version_index
        .get(&key)
        .ok_or_else(|| eyre::eyre!("Could not find required game version '{name}'"))?;

    let matches: Vec<&CurseforgeGameVersion> = candidates
        .iter()
        .filter(|candidate| {
            candidate.name == name && candidate.game_version_type_id == Some(required_type_id)
        })
        .collect();

    match matches.as_slice() {
        [single] => Ok(single.id),
        [] => eyre::bail!(
            "Could not resolve required game version '{}' with type id {}. Candidates: {}",
            name,
            required_type_id,
            format_candidates(candidates)
        ),
        _ => {
            let summary = matches
                .iter()
                .map(|candidate| candidate.id.to_string())
                .collect::<Vec<_>>()
                .join(", ");
            eyre::bail!(
                "Ambiguous game version '{}' with type id {}. Matching IDs: {}",
                name,
                required_type_id,
                summary
            )
        }
    }
}

pub(super) fn resolve_minecraft_version_id(
    game_version_index: &HashMap<String, Vec<CurseforgeGameVersion>>,
    minecraft_version: &str,
) -> eyre::Result<u64> {
    let key = normalize_version_key(minecraft_version);
    let candidates = game_version_index.get(&key).ok_or_else(|| {
        eyre::eyre!("Could not find required Minecraft version '{minecraft_version}'")
    })?;

    let matches: Vec<&CurseforgeGameVersion> = candidates
        .iter()
        .filter(|candidate| {
            candidate.name == minecraft_version
                && candidate
                    .game_version_type_id
                    .is_some_and(|type_id| type_id != 1 && type_id != 615)
                && parse_version(&candidate.name).is_some()
        })
        .collect();

    match matches.as_slice() {
        [single] => Ok(single.id),
        [] => eyre::bail!(
            "Could not resolve required Minecraft version '{}'. Candidates: {}",
            minecraft_version,
            format_candidates(candidates)
        ),
        _ => {
            let summary = matches
                .iter()
                .map(|candidate| {
                    let type_id = candidate
                        .game_version_type_id
                        .map_or_else(|| "<none>".to_string(), |id| id.to_string());
                    format!("{}(type={})", candidate.id, type_id)
                })
                .collect::<Vec<_>>()
                .join(", ");
            eyre::bail!(
                "Ambiguous Minecraft version '{}'. Matching IDs: {}",
                minecraft_version,
                summary
            )
        }
    }
}

pub(super) fn resolve_game_version_ids(
    game_version_index: &HashMap<String, Vec<CurseforgeGameVersion>>,
    names: &[String],
) -> eyre::Result<Vec<u64>> {
    let mut output = Vec::with_capacity(names.len());
    for name in names {
        let id = match name.as_str() {
            "Forge" => resolve_exact_type_id(game_version_index, "Forge", 68_441)?,
            "NeoForge" => resolve_exact_type_id(game_version_index, "NeoForge", 68_441)?,
            "Client" => resolve_exact_type_id(game_version_index, "Client", 75_208)?,
            "Server" => resolve_exact_type_id(game_version_index, "Server", 75_208)?,
            "Java 17" => resolve_exact_type_id(game_version_index, "Java 17", 2)?,
            "Java 21" => resolve_exact_type_id(game_version_index, "Java 21", 2)?,
            "Java 25" => resolve_exact_type_id(game_version_index, "Java 25", 2)?,
            minecraft if parse_version(minecraft).is_some() => {
                resolve_minecraft_version_id(game_version_index, minecraft)?
            }
            _ => eyre::bail!("Unsupported release metadata name '{name}'"),
        };

        if !output.contains(&id) {
            output.push(id);
        }
    }
    Ok(output)
}

pub(super) fn normalize_version_key(input: &str) -> String {
    input.trim().to_ascii_lowercase().replace([' ', '_'], "-")
}

pub(super) fn game_version_names_for_release(mc_version: &str) -> eyre::Result<Vec<String>> {
    let parsed = parse_version(mc_version)
        .ok_or_else(|| eyre::eyre!("Could not parse MC version '{mc_version}'"))?;

    let mut names = vec![
        "Client".to_string(),
        "Server".to_string(),
        mc_version.to_string(),
    ];
    names.extend(loader_names_for(parsed).into_iter().map(str::to_string));
    names.push(java_version_name_for(parsed).to_string());
    Ok(names)
}

pub(super) fn loader_names_for(parsed: (u32, u32, u32)) -> Vec<&'static str> {
    let v_1_20_0 = (1, 20, 0);
    let v_1_20_1 = (1, 20, 1);

    if parsed <= v_1_20_0 {
        return vec!["Forge"];
    }

    if parsed == v_1_20_1 {
        return vec!["Forge", "NeoForge"];
    }

    vec!["NeoForge"]
}

pub(super) fn java_version_name_for(parsed: (u32, u32, u32)) -> &'static str {
    let v_1_20_4 = (1, 20, 4);
    let v_26_0_0 = (26, 0, 0);
    if parsed <= v_1_20_4 {
        "Java 17"
    } else if parsed >= v_26_0_0 {
        "Java 25"
    } else {
        "Java 21"
    }
}

impl CurseforgeMinecraftCommand {
    /// # Errors
    ///
    /// This function will return an error if the subcommand fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            Self::Version(args) => args.invoke(),
        }
    }
}

impl CurseforgeMinecraftVersionCommand {
    /// # Errors
    ///
    /// This function will return an error if the subcommand fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            Self::List(args) => args.invoke(),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::format_age;
    use super::java_version_name_for;
    use super::parse_safety_age;
    use std::time::Duration;

    #[test]
    fn parse_safety_age_supports_seconds_minutes_and_hours() {
        assert_eq!(parse_safety_age("45s").unwrap(), Duration::from_secs(45));
        assert_eq!(parse_safety_age("30m").unwrap(), Duration::from_mins(30));
        assert_eq!(parse_safety_age("2h").unwrap(), Duration::from_hours(2));
    }

    #[test]
    fn parse_safety_age_rejects_invalid_inputs() {
        let _ = parse_safety_age("").unwrap_err();
        let _ = parse_safety_age("30").unwrap_err();
        let _ = parse_safety_age("30x").unwrap_err();
    }

    #[test]
    fn format_age_uses_readable_units() {
        assert_eq!(format_age(Duration::from_secs(5)), "5s");
        assert_eq!(format_age(Duration::from_secs(90)), "1m30s");
        assert_eq!(format_age(Duration::from_mins(121)), "2h1m");
    }

    #[test]
    fn java_version_metadata_tracks_supported_minecraft_lines() {
        assert_eq!(java_version_name_for((1, 20, 4)), "Java 17");
        assert_eq!(java_version_name_for((1, 21, 1)), "Java 21");
        assert_eq!(java_version_name_for((26, 1, 2)), "Java 25");
    }
}
