#![allow(clippy::doc_markdown)]

use crate::cli::curseforge::CurseforgeMinecraftVersionCommand;
use facet::Facet;
use figue as args;

/// Arguments for CurseForge Minecraft version operations.
#[derive(Facet, Debug)]
pub struct CurseforgeMinecraftVersionArgs {
    /// Version subcommand.
    #[facet(args::subcommand)]
    pub command: CurseforgeMinecraftVersionCommand,
}

impl CurseforgeMinecraftVersionArgs {
    /// # Errors
    ///
    /// Returns an error if the selected Minecraft version command fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}
