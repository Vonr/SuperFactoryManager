use crate::cancellation::CancellationToken;
use crate::cli::jar::JarBuildOptionsArgs;
use crate::jar_build::BuildMode;
use crate::jar_build::BuildOptions;
use crate::jar_build::RunCommand;
use crate::jar_build::RunKind;
use facet::Facet;

/// Arguments for compiling and running the Java JUnit test source set.
#[derive(Facet, Debug, Clone)]
pub struct RunTestArgs {
    /// Build and test options.
    #[facet(flatten)]
    pub options: JarBuildOptionsArgs,
}

impl RunTestArgs {
    pub(crate) fn into_options(self, mode: BuildMode) -> eyre::Result<BuildOptions> {
        self.options.into_options(mode)
    }

    /// # Errors
    ///
    /// Returns an error if planning, building, compiling tests, or running JUnit fails.
    pub fn invoke(self, cancellation_token: CancellationToken) -> eyre::Result<()> {
        RunCommand::new(
            self.into_options(BuildMode::Build)?,
            RunKind::Test,
            cancellation_token,
        )
        .invoke()
    }
}
