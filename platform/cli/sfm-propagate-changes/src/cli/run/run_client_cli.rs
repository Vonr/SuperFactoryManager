use crate::cancellation::CancellationToken;
use crate::cli::jar::JarBuildOptionsArgs;
use crate::jar_build::BuildMode;
use crate::jar_build::BuildOptions;
use crate::jar_build::RunCommand;
use crate::jar_build::RunKind;
use crate::jar_build::RunOptions;
use facet::Facet;
use figue as args;

/// Arguments for launching the Forge client userdev run config.
#[derive(Facet, Debug, Clone)]
pub struct RunClientArgs {
    /// Build and launch options.
    #[facet(flatten)]
    pub options: JarBuildOptionsArgs,
    /// Open the SFM text editor when the client first reaches the title screen.
    #[facet(default, args::named)]
    pub text_editor: bool,
}

impl RunClientArgs {
    pub(crate) fn into_options(self, mode: BuildMode) -> eyre::Result<BuildOptions> {
        self.options.into_options(mode)
    }

    /// # Errors
    ///
    /// Returns an error if planning, building, or launching fails.
    pub fn invoke(self, cancellation_token: CancellationToken) -> eyre::Result<()> {
        let text_editor = self.text_editor;
        RunCommand::with_run_options(
            self.into_options(BuildMode::Build)?,
            RunKind::Client,
            RunOptions {
                open_text_editor_on_title_screen: text_editor,
                ..RunOptions::default()
            },
            cancellation_token,
        )
        .invoke()
    }
}
