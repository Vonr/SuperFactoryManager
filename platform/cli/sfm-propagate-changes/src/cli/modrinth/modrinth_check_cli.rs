use crate::cli::jar::BranchSelector;
use facet::Facet;
use figue as args;

/// Arguments for checking computed Modrinth release metadata.
#[derive(Facet, Debug)]
pub struct ModrinthCheckArgs {
    /// Branch selector used to choose release jar Minecraft versions. Defaults to `core`.
    #[facet(default, args::named)]
    pub branch: BranchSelector,

    /// Modrinth project id/slug (defaults to Super Factory Manager).
    #[facet(default, args::named)]
    pub project: Option<String>,
}

impl ModrinthCheckArgs {
    /// # Errors
    ///
    /// Returns an error if release metadata does not match historical project versions.
    pub fn invoke(self) -> eyre::Result<()> {
        super::modrinth_cli::check_release_metadata(self.branch, self.project)
    }
}
