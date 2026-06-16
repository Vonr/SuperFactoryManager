#![allow(clippy::doc_markdown)]

use crate::cli::curseforge::CurseforgeProjectFileCommand;
use facet::Facet;
use figue as args;

/// Arguments for CurseForge project file operations.
#[derive(Facet, Debug)]
pub struct CurseforgeProjectFileArgs {
    /// File subcommand.
    #[facet(args::subcommand)]
    pub command: CurseforgeProjectFileCommand,
}

impl CurseforgeProjectFileArgs {
    /// # Errors
    ///
    /// Returns an error if the selected file command fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}
