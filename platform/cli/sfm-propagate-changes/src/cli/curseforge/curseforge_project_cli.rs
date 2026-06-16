#![allow(clippy::doc_markdown)]

use crate::cli::curseforge::CurseforgeProjectCommand;
use facet::Facet;
use figue as args;

/// Arguments for CurseForge project operations.
#[derive(Facet, Debug)]
pub struct CurseforgeProjectArgs {
    /// Project subcommand.
    #[facet(args::subcommand)]
    pub command: CurseforgeProjectCommand,
}

impl CurseforgeProjectArgs {
    /// # Errors
    ///
    /// Returns an error if the selected project command fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}
