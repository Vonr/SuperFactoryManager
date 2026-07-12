use super::DependencySourceAcquireArgs;
use super::DependencySourceConfigureArgs;
use super::DependencySourceProviderArgs;
use crate::cancellation::CancellationToken;
use crate::paths::CacheHome;
use facet::Facet;
use figue as args;

#[derive(Facet, Debug)]
pub struct DependencySourceArgs {
    #[facet(args::subcommand)]
    pub command: DependencySourceCommand,
}

#[derive(Facet, Debug)]
#[repr(u8)]
pub enum DependencySourceCommand {
    /// Acquire source trees from already locked provider metadata.
    Acquire(DependencySourceAcquireArgs),
    /// Configure and lock source-provider intent and evidence.
    Configure(DependencySourceConfigureArgs),
    /// Inspect configured source providers.
    Provider(DependencySourceProviderArgs),
}

impl DependencySourceArgs {
    pub(crate) fn invoke(
        self,
        cancellation_token: &CancellationToken,
        cache_home: &CacheHome,
    ) -> eyre::Result<()> {
        match self.command {
            DependencySourceCommand::Acquire(args) => args.invoke(cancellation_token, cache_home),
            DependencySourceCommand::Configure(args) => args.invoke(cancellation_token, cache_home),
            DependencySourceCommand::Provider(args) => args.invoke(cancellation_token, cache_home),
        }
    }
}
