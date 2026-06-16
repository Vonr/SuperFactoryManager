use crate::cli::curseforge::CurseforgeMinecraftVersionCommand;
use crate::cli::jar::BranchSelector;

pub(super) fn invoke(command: CurseforgeMinecraftVersionCommand) -> eyre::Result<()> {
    command.invoke()
}

pub(super) fn invoke_list( // todo(2026-06-16) should be its own Args struct with an instance method for invoke
    branch: BranchSelector,
    token: Option<String>,
    op_secret: Option<String>,
) -> eyre::Result<()> {
    super::curseforge_command::invoke_minecraft_version_list(branch, token, op_secret)
}
