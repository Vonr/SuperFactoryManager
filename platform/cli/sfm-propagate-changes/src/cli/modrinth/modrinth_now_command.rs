use crate::cli::jar::BranchSelector;

pub(super) fn invoke(
    branch: BranchSelector,
    project: Option<String>,
    token: Option<String>,
    op_secret: Option<String>,
    dry_run: bool,
) -> eyre::Result<()> {
    super::modrinth_command::invoke_now(branch, project, token, op_secret, dry_run)
}
// todo(2026-06-16) cli args struct
