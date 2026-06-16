use crate::cli::jar::BranchSelector;
use facet::Facet;
use figue as args;

/// Arguments for amending Modrinth changelogs for current release jars.
#[derive(Facet, Debug)]
pub struct ModrinthAmendArgs {
    /// Branch selector used to choose release jar Minecraft versions. Defaults to `core`.
    #[facet(default, args::named)]
    pub branch: BranchSelector,

    /// Modrinth project id/slug (defaults to Super Factory Manager).
    #[facet(default, args::named)]
    pub project: Option<String>,

    /// Modrinth API token; if omitted, `MODRINTH_TOKEN` is used, then 1Password lookup.
    #[facet(default, args::named)]
    pub token: Option<String>,

    /// 1Password secret reference used when token is omitted.
    #[facet(default, args::named, rename = "op-secret")]
    pub op_secret: Option<String>,

    /// Resolve remote target and print the amend plan without updating Modrinth.
    #[facet(default, args::named, rename = "dry-run")]
    pub dry_run: bool,
}

impl ModrinthAmendArgs {
    /// # Errors
    ///
    /// Returns an error if the remote targets cannot be resolved or amended.
    pub fn invoke(self) -> eyre::Result<()> {
        super::modrinth_cli::release_amend(
            self.branch,
            self.project,
            self.token,
            self.op_secret,
            self.dry_run,
        )
    }
}
