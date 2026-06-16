use crate::cli::modrinth::ModrinthReleaseCommand;
use facet::Facet;
use figue as args;

/// Arguments for Modrinth release operations.
#[derive(Facet, Debug)]
pub struct ModrinthReleaseArgs {
    /// Release subcommand.
    #[facet(args::subcommand)]
    pub command: ModrinthReleaseCommand,
}

impl ModrinthReleaseArgs {
    /// # Errors
    ///
    /// Returns an error if the selected release operation fails.
    pub fn invoke(self) -> eyre::Result<()> {
        self.command.invoke()
    }
}
