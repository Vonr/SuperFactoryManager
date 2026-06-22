use crate::cancellation::CancellationToken;
use crate::cli::jar::JarBuildOptionsArgs;
use crate::jar_build::BuildMode;
use crate::jar_build::BuildOptions;
use crate::jar_build::RunCommand;
use crate::jar_build::RunKind;
use crate::jar_build::RunOptions;
use facet::Facet;
use figue as args;

/// Arguments for launching the Forge client and running SFM game tests.
#[derive(Facet, Debug, Clone)]
pub struct RunClientPuppetArgs {
    /// Build and launch options.
    #[facet(flatten)]
    pub options: JarBuildOptionsArgs,

    /// Run only SFM game tests matching this selector. Supports unqualified names, `*`, `?`, and comma-separated selectors.
    #[facet(default, args::named)]
    pub filter: Option<String>,
}

impl RunClientPuppetArgs {
    pub(crate) fn into_options(self, mode: BuildMode) -> eyre::Result<BuildOptions> {
        self.options.into_options(mode)
    }

    /// # Errors
    ///
    /// Returns an error if planning, building, launching, or game-test validation fails.
    pub fn invoke(self, cancellation_token: CancellationToken) -> eyre::Result<()> {
        let filter = self.filter.clone();
        RunCommand::with_run_options(
            self.into_options(BuildMode::Build)?,
            RunKind::ClientPuppet,
            RunOptions {
                game_test_filter: filter,
                game_test_bisect: None,
            },
            cancellation_token,
        )
        .invoke()
    }
}
