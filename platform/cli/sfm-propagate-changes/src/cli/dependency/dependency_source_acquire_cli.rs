use super::dependency_context::load_inventory;
use crate::cancellation::CancellationToken;
use crate::cli::jar::BranchSelector;
use crate::paths::CacheHome;
use crate::payload_fetcher::http_fetcher;
use crate::source_maven::acquire_locked_maven_sources;
use crate::terminal_output::stdout_line;
use crate::toolchain_lockfile_schema::version::v3::SourceProviderV3;
use facet::Facet;
use figue as args;

#[derive(Facet, Debug)]
pub struct DependencySourceAcquireArgs {
    /// Dependency or dependency/component to acquire.
    #[facet(args::positional)]
    pub target: String,
    /// Built-in provider kind. Defaults to `any`.
    #[facet(default, args::named)]
    pub provider: Option<DependencySourceProviderSelector>,
    /// Stable provider ID, for selecting among providers of the same kind.
    #[facet(default, args::named)]
    pub provider_id: Option<String>,
    /// Branch selector to read. Must match exactly one worktree.
    #[facet(args::named)]
    pub branch: BranchSelector,
}

impl DependencySourceAcquireArgs {
    pub(crate) fn invoke(
        self,
        cancellation_token: &CancellationToken,
        cache_home: &CacheHome,
    ) -> eyre::Result<()> {
        let inventory = load_inventory(self.branch, cache_home)?;
        let (dependency, component) = select_component(&inventory, &self.target)?;
        if self.provider.is_some() && self.provider_id.is_some() {
            eyre::bail!("Use either --provider or --provider-id, not both.");
        }
        let selector = self
            .provider
            .unwrap_or(DependencySourceProviderSelector::Any);
        let provider = component
            .source_providers
            .iter()
            .find(|provider| {
                self.provider_id.as_deref().map_or_else(
                    || provider_matches(provider, selector),
                    |id| provider_id(provider) == id,
                )
            })
            .ok_or_else(|| {
                eyre::eyre!(
                    "No configured source provider matching '{}' for {}/{}.",
                    self.provider_id.as_deref().unwrap_or(selector.label()),
                    dependency.id,
                    component.id
                )
            })?;
        match provider {
            SourceProviderV3::MavenSources(provider) => acquire_locked_maven_sources(
                &inventory,
                provider,
                cancellation_token,
                &http_fetcher()?,
            )?,
            SourceProviderV3::Git(_) => {
                eyre::bail!("Git source acquisition is not implemented yet.")
            }
            SourceProviderV3::Decompile(_) => {
                eyre::bail!("Decompiled source acquisition is not implemented yet.")
            }
            SourceProviderV3::PlatformPipeline(_) => {
                eyre::bail!("Platform source acquisition is not implemented yet.")
            }
        }
        let view = inventory
            .source_providers(component)
            .find(|view| view.id() == provider_id(provider))
            .expect("selected provider belongs to component");
        for root in view.searchable_roots() {
            stdout_line(format!("{}: {}", view.id(), root.display()))?;
        }
        Ok(())
    }
}

#[derive(Clone, Copy, Debug, Eq, Facet, PartialEq)]
#[facet(rename_all = "kebab-case")]
#[repr(u8)]
pub enum DependencySourceProviderSelector {
    Any,
    MavenSources,
    Git,
    Decompile,
    PlatformPipeline,
}

impl DependencySourceProviderSelector {
    const fn label(self) -> &'static str {
        match self {
            Self::Any => "any",
            Self::MavenSources => "maven-sources",
            Self::Git => "git",
            Self::Decompile => "decompile",
            Self::PlatformPipeline => "platform-pipeline",
        }
    }
}

fn provider_matches(
    provider: &SourceProviderV3,
    selector: DependencySourceProviderSelector,
) -> bool {
    matches!(selector, DependencySourceProviderSelector::Any)
        || matches!(
            (provider, selector),
            (
                SourceProviderV3::MavenSources(_),
                DependencySourceProviderSelector::MavenSources
            )
        )
        || matches!(
            (provider, selector),
            (
                SourceProviderV3::Git(_),
                DependencySourceProviderSelector::Git
            )
        )
        || matches!(
            (provider, selector),
            (
                SourceProviderV3::Decompile(_),
                DependencySourceProviderSelector::Decompile
            )
        )
        || matches!(
            (provider, selector),
            (
                SourceProviderV3::PlatformPipeline(_),
                DependencySourceProviderSelector::PlatformPipeline
            )
        )
}

fn provider_id(provider: &SourceProviderV3) -> &str {
    match provider {
        SourceProviderV3::MavenSources(provider) => &provider.id,
        SourceProviderV3::Git(provider) => &provider.id,
        SourceProviderV3::Decompile(provider) => &provider.id,
        SourceProviderV3::PlatformPipeline(provider) => &provider.id,
    }
}

fn select_component<'a>(
    inventory: &'a crate::dependency_inventory::DependencyInventory,
    target: &str,
) -> eyre::Result<(
    &'a crate::toolchain_lockfile_schema::version::v3::DependencyV3,
    &'a crate::toolchain_lockfile_schema::version::v3::DependencyComponentV3,
)> {
    let mut parts = target.split('/');
    let dependency_id = parts.next().unwrap_or_default();
    let component_id = parts.next();
    if dependency_id.is_empty() || parts.next().is_some() {
        eyre::bail!("Expected dependency or dependency/component, got '{target}'.");
    }
    let dependency = inventory.dependency(dependency_id)?;
    let component = match component_id {
        Some(component_id) => dependency
            .components
            .iter()
            .find(|component| component.id == component_id)
            .ok_or_else(|| eyre::eyre!("Unknown component '{target}'."))?,
        None if dependency.components.len() == 1 => &dependency.components[0],
        None => eyre::bail!(
            "Dependency '{dependency_id}' has multiple components; select dependency/component explicitly."
        ),
    };
    Ok((dependency, component))
}
