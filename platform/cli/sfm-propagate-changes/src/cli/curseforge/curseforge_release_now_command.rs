use crate::cli::jar::BranchSelector;

pub(super) fn invoke(// todo(2026-06-16) should be its own Args struct with an instance method for invoke
    branch: BranchSelector,
    project: Option<u64>,
    token: Option<String>,
    op_secret: Option<String>,
    dry_run: bool,
) -> eyre::Result<()> {
    super::curseforge_command::invoke_release_now(branch, project, token, op_secret, dry_run)
}
