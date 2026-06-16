#![allow(clippy::doc_markdown)]

use crate::cli::jar::BranchSelector;
use facet::Facet;
use figue as args;

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
        super::curseforge_cli::release_amend(
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
