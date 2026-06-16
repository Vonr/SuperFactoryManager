use crate::cli::jar::BranchSelector;

pub(super) fn invoke(// todo(2026-06-16) cli args struct
    branch: BranchSelector,
    project: Option<String>,
    token: Option<String>,
    op_secret: Option<String>,
    dry_run: bool,
) -> eyre::Result<()> {
    super::modrinth_command::invoke_amend(branch, project, token, op_secret, dry_run)
}
