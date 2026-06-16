use crate::cli::jar::BranchSelector;

pub(super) fn invoke( // todo(2026-06-16) should be its own Args struct with an instance method for invoke
    branch: BranchSelector,
    project: Option<u64>,
    api_key: Option<String>,
    token: Option<String>,
    op_secret: Option<String>,
) -> eyre::Result<()> {
    super::curseforge_command::invoke_release_check(branch, project, api_key, token, op_secret)
}
