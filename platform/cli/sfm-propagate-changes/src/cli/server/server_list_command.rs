use crate::cli::jar::BranchSelector;

pub(super) fn invoke(glob: Option<String>, branch: BranchSelector) -> eyre::Result<()> {
    let glob = glob.unwrap_or_else(|| "*".to_string());
    super::server_command::list_servers(&glob, branch)
}
// todo(2026-06-16) cli args struct
