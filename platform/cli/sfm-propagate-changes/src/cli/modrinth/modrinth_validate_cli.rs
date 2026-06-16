use crate::cli::jar::BranchSelector;
use facet::Facet;
use figue as args;

/// Arguments for validating remote Modrinth jars against local release jars.
#[derive(Facet, Debug)]
pub struct ModrinthValidateArgs {
    /// Branch selector used to choose release jar Minecraft versions. Defaults to `core`.
    #[facet(default, args::named)]
    pub branch: BranchSelector,

    /// Modrinth project id/slug (defaults to Super Factory Manager).
    #[facet(default, args::named)]
    pub project: Option<String>,
}

impl ModrinthValidateArgs {
    /// # Errors
    ///
    /// Returns an error if remote hashes do not match local release jars.
    pub fn invoke(self) -> eyre::Result<()> {
        super::modrinth_cli::validate_release_hashes(self.branch, self.project)
    }
}
