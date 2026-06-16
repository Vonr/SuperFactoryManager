#![allow(clippy::doc_markdown)]

use crate::cli::curseforge::CurseforgeReleaseCommand;
use facet::Facet;
use figue as args;

/// Arguments for CurseForge release operations.
#[derive(Facet, Debug)]
pub struct CurseforgeReleaseArgs {
    /// Release subcommand.
    #[facet(args::subcommand)]
    pub command: CurseforgeReleaseCommand,
}

impl CurseforgeReleaseArgs {
    /// # Errors
    ///
    /// Returns an error if the selected CurseForge release operation fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}
