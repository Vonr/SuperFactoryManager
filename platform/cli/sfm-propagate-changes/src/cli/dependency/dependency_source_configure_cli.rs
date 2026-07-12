use super::dependency_context::load_inventory;
use crate::cancellation::CancellationToken;
use crate::cli::jar::BranchSelector;
use crate::paths::CacheHome;
use crate::payload_fetcher::http_fetcher;
use crate::source_maven::configure_maven_sources;
use crate::terminal_output::stdout_line;
use crate::toolchain_lockfile_schema::version::v3::SourceProviderV3;
use crate::toolchain_lockfile_write::write_lockfile_atomically;
use facet::Facet;
use figue as args;

#[derive(Facet, Debug)]
pub struct DependencySourceConfigureArgs {
    /// Dependency or dependency/component to configure.
    #[facet(args::positional)]
    pub target: String,
    /// Branch selector to update. Must match exactly one worktree.
    #[facet(args::named)]
    pub branch: BranchSelector,
    /// Configure the conventional Maven `sources` classifier.
    #[facet(default = false, args::named)]
    pub maven_sources: bool,
    /// Exact alternate Maven source coordinate for unusual publications.
    #[facet(default, args::named)]
    pub maven_coordinate: Option<String>,
    /// Search root relative to the extracted source tree. Repeat as needed.
    #[facet(default, args::named)]
    pub root: Vec<String>,
}

impl DependencySourceConfigureArgs {
    pub(crate) fn invoke(
        self,
        cancellation_token: &CancellationToken,
        cache_home: &CacheHome,
    ) -> eyre::Result<()> {
        if !self.maven_sources && self.maven_coordinate.is_none() {
            eyre::bail!("Specify --maven-sources or --maven-coordinate.");
        }
        let mut inventory = load_inventory(self.branch, cache_home)?;
        let (dependency_id, component_id) = split_target(&self.target)?;
        let dependency_index = inventory
            .lockfile
            .dependencies
            .iter()
            .position(|dependency| dependency.id == dependency_id)
            .ok_or_else(|| eyre::eyre!("Unknown dependency '{dependency_id}'."))?;
        let component_index = select_component_index(
            &inventory.lockfile.dependencies[dependency_index].components,
            dependency_id,
            component_id,
        )?;
        let provider = configure_maven_sources(
            &inventory,
            &inventory.lockfile.dependencies[dependency_index].components[component_index],
            self.maven_coordinate.as_deref(),
            self.root,
            cancellation_token,
            &http_fetcher()?,
        )?
        .ok_or_else(|| {
            eyre::eyre!(
                "No Maven source payload was published for '{}'. Configure Git or decompile sources instead.",
                self.target
            )
        })?;
        let coordinate = provider.derived_checks.resolved_coordinate.clone();
        let providers = &mut inventory.lockfile.dependencies[dependency_index].components
            [component_index]
            .source_providers;
        providers.retain(|provider| {
            !matches!(provider, SourceProviderV3::MavenSources(provider) if provider.id == "maven-sources")
        });
        providers.push(SourceProviderV3::MavenSources(provider));
        let output = inventory.lockfile.to_canonical_json()?;
        write_lockfile_atomically(
            &inventory.lockfile_path,
            &inventory.original_input,
            output.as_bytes(),
        )?;
        stdout_line(format!(
            "Configured Maven sources for {}: {}",
            self.target, coordinate
        ))?;
        Ok(())
    }
}

fn split_target(target: &str) -> eyre::Result<(&str, Option<&str>)> {
    let mut parts = target.split('/');
    let dependency = parts.next().unwrap_or_default();
    let component = parts.next();
    if dependency.is_empty() || parts.next().is_some() || component.is_some_and(str::is_empty) {
        eyre::bail!("Expected dependency or dependency/component, got '{target}'.");
    }
    Ok((dependency, component))
}

fn select_component_index(
    components: &[crate::toolchain_lockfile_schema::version::v3::DependencyComponentV3],
    dependency_id: &str,
    component_id: Option<&str>,
) -> eyre::Result<usize> {
    if let Some(component_id) = component_id {
        return components
            .iter()
            .position(|component| component.id == component_id)
            .ok_or_else(|| eyre::eyre!("Unknown component '{dependency_id}/{component_id}'."));
    }
    if components.len() != 1 {
        eyre::bail!(
            "Dependency '{dependency_id}' has multiple components; select dependency/component explicitly."
        );
    }
    Ok(0)
}
