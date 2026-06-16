#![allow(clippy::doc_markdown)]

use facet::Facet;
use figue as args;

/// Arguments for listing CurseForge project files.
#[derive(Facet, Debug)]
pub struct CurseforgeProjectFileListArgs {
    /// CurseForge project ID (defaults to configured default project).
    #[facet(default, args::named)]
    pub project: Option<u64>,

    /// CurseForge Core API key; if omitted, CURSEFORGE_CORE_API_KEY is used.
    #[facet(default, args::named, rename = "api-key")]
    pub api_key: Option<String>,

    /// CurseForge API token; if omitted, CURSEFORGE_API_TOKEN is used, then 1Password lookup.
    #[facet(default, args::named)]
    pub token: Option<String>,

    /// 1Password secret reference used when token is omitted.
    #[facet(default, args::named, rename = "op-secret")]
    pub op_secret: Option<String>,
}

impl CurseforgeProjectFileListArgs {
    /// # Errors
    ///
    /// Returns an error if CurseForge project files cannot be queried.
    pub fn invoke(self) -> eyre::Result<()> {
        super::curseforge_cli::list_project_files(
            self.project,
            self.api_key,
            self.token,
            self.op_secret,
        )
    }
}
