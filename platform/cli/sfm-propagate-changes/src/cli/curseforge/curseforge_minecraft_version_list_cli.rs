#![allow(clippy::doc_markdown)]

use crate::cli::jar::BranchSelector;
use facet::Facet;
use figue as args;

/// Arguments for listing Minecraft game versions from CurseForge.
#[derive(Facet, Debug)]
pub struct CurseforgeMinecraftVersionListArgs {
    /// Branch selector expression. Defaults to core worktrees.
    #[facet(default, args::named)]
    pub branch: BranchSelector,

    /// CurseForge API token (optional for this endpoint).
    #[facet(default, args::named)]
    pub token: Option<String>,

    /// 1Password secret reference used when token is omitted and env var is missing.
    #[facet(default, args::named, rename = "op-secret")]
    pub op_secret: Option<String>,
}

impl CurseforgeMinecraftVersionListArgs {
    /// # Errors
    ///
    /// Returns an error if Minecraft versions cannot be queried.
    pub fn invoke(self) -> eyre::Result<()> {
        super::curseforge_cli::list_minecraft_versions(self.branch, self.token, self.op_secret)
    }
}
