use crate::cli::jar::BranchSelector;

pub(super) fn invoke(branch: BranchSelector) -> eyre::Result<()> {
    super::server_command::launch_servers(branch)
}
// todo(2026-06-16) cli args struct
