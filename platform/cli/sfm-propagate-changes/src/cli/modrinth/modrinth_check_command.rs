use crate::cli::jar::BranchSelector;

pub(super) fn invoke(branch: BranchSelector, project: Option<String>) -> eyre::Result<()> {
    super::modrinth_command::invoke_check(branch, project)
}
// todo(2026-06-16) cli args struct