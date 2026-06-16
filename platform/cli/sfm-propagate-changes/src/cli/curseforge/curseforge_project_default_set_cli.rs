#![allow(clippy::doc_markdown)]

use facet::Facet;
use figue as args;

/// Arguments for setting the default CurseForge project ID.
#[derive(Facet, Debug)]
pub struct CurseforgeProjectDefaultSetArgs {
    /// Project ID to persist as default.
    #[facet(args::positional)]
    pub project: u64,
}

impl CurseforgeProjectDefaultSetArgs {
    /// # Errors
    ///
    /// Returns an error if the default project ID cannot be persisted.
    pub fn invoke(self) -> eyre::Result<()> {
        super::curseforge_cli::set_default_project_id(self.project)
    }
}
