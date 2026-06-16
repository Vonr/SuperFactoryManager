#![allow(clippy::doc_markdown)]

use crate::cli::curseforge::CurseforgeProjectDefaultCommand;
use facet::Facet;
use figue as args;

/// Arguments for CurseForge default-project operations.
#[derive(Facet, Debug)]
pub struct CurseforgeProjectDefaultArgs {
    /// Default-project subcommand.
    #[facet(args::subcommand)]
    pub command: CurseforgeProjectDefaultCommand,
}

impl CurseforgeProjectDefaultArgs {
    /// # Errors
    ///
    /// Returns an error if the selected default-project command fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}
