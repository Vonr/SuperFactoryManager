#![allow(clippy::doc_markdown)]

use crate::cli::curseforge::CurseforgeMinecraftCommand;
use facet::Facet;
use figue as args;

/// Arguments for CurseForge Minecraft metadata operations.
#[derive(Facet, Debug)]
pub struct CurseforgeMinecraftArgs {
    /// Minecraft subcommand.
    #[facet(args::subcommand)]
    pub command: CurseforgeMinecraftCommand,
}

impl CurseforgeMinecraftArgs {
    /// # Errors
    ///
    /// Returns an error if the selected Minecraft metadata command fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}
