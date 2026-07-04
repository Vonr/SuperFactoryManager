use crate::cancellation::CancellationToken;
use crate::cli::jar::JarBuildOptionsArgs;
use crate::jar_build::BuildMode;
use crate::jar_build::BuildOptions;
use crate::jar_build::ClientTitleScreen;
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
    /// Open the input diagnostics screen when the client first reaches the title screen.
    #[facet(default, args::named)]
    pub input_diag: bool,
    /// Open a supported SFM dev screen when the client first reaches the title screen.
    #[facet(default, args::named)]
    pub title_screen: Option<ClientTitleScreen>,
}

impl RunClientArgs {
    pub(crate) fn into_options(self, mode: BuildMode) -> eyre::Result<BuildOptions> {
        self.options.into_options(mode)
    }

    /// # Errors
    ///
    /// Returns an error if planning, building, or launching fails.
    pub fn invoke(self, cancellation_token: CancellationToken) -> eyre::Result<()> {
        let title_screen = self.resolve_title_screen()?;
        RunCommand::with_run_options(
            self.into_options(BuildMode::Build)?,
            RunKind::Client,
            RunOptions {
                client_title_screen: title_screen,
                ..RunOptions::default()
            },
            cancellation_token,
        )
        .invoke()
    }

    fn resolve_title_screen(&self) -> eyre::Result<Option<ClientTitleScreen>> {
        let mut selected = Vec::new();
        if let Some(title_screen) = self.title_screen {
            selected.push(("--title-screen", title_screen));
        }
        if self.text_editor {
            selected.push(("--text-editor", ClientTitleScreen::TextEditor));
        }
        if self.input_diag {
            selected.push(("--input-diag", ClientTitleScreen::InputDiag));
        }
        if selected.len() > 1 {
            let flags = selected
                .iter()
                .map(|(flag, _)| *flag)
                .collect::<Vec<_>>()
                .join(", ");
            eyre::bail!("Only one title-screen dev screen can be selected; got {flags}.");
        }
        Ok(selected.into_iter().next().map(|(_, screen)| screen))
    }
}
