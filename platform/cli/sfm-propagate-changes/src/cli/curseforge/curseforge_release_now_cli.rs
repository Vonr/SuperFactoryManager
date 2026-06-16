#![allow(clippy::doc_markdown)]

use crate::cli::jar::BranchSelector;
use facet::Facet;
use figue as args;

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
        super::curseforge_cli::release_now(
            self.branch,
            self.project,
            self.token,
            self.op_secret,
            self.dry_run,
        )
    }
}
