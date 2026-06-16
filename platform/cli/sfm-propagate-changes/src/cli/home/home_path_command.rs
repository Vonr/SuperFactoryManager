use crate::paths::APP_HOME;
use crate::terminal_output::stdout_line;

pub(super) fn invoke() -> eyre::Result<()> { // todo(2026-06-16) cli args struct problem
    stdout_line(APP_HOME.0.display())
}
