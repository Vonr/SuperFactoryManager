#![allow(clippy::doc_markdown)]

use crate::cli::jar::BranchSelector;
use facet::Facet;
use figue as args;

/// Arguments for validating remote CurseForge files against local release jars.
#[derive(Facet, Debug)]
pub struct CurseforgeReleaseValidateArgs {
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
}

impl CurseforgeReleaseValidateArgs {
    /// # Errors
    ///
    /// Returns an error if remote file hashes do not match local release jars.
    pub fn invoke(self) -> eyre::Result<()> {
        super::curseforge_cli::validate_release_hashes(
            self.branch,
            self.project,
            self.api_key,
            self.token,
            self.op_secret,
        )
    }
}
