use crate::terminal_output::stdout_line;

pub(super) fn invoke() -> eyre::Result<()> {
    let path = super::get_repo_root()?;
    stdout_line(path.display())
}
// todo(2026-06-16) cli args struct
