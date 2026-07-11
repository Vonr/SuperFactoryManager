use super::dependency_context::load_inventory;
use crate::cancellation::CancellationToken;
use crate::cli::jar::BranchSelector;
use crate::dependency_inventory::DependencyInventory;
use crate::jar_build::hash::ContentHash;
use crate::jar_build::hash::ContentHashAlgorithm;
use crate::paths::CacheHome;
use crate::terminal_output::stdout_line;
use crate::toolchain_lockfile_schema::version::v3::ArtifactOwnerV3;
use crate::toolchain_lockfile_schema::version::v3::ArtifactProvenanceV3;
use crate::toolchain_lockfile_schema::version::v3::ArtifactPurposeV3;
use crate::toolchain_lockfile_schema::version::v3::ArtifactTreatmentV3;
use crate::toolchain_lockfile_schema::version::v3::ArtifactV3;
use crate::toolchain_lockfile_schema::version::v3::ComponentAcquisitionV3;
use crate::toolchain_lockfile_schema::version::v3::ComponentDeclarationV3;
use crate::toolchain_lockfile_schema::version::v3::ComponentDerivedChecksV3;
use crate::toolchain_lockfile_schema::version::v3::DataRunPolicyV3;
use crate::toolchain_lockfile_schema::version::v3::DependencyComponentV3;
use crate::toolchain_lockfile_schema::version::v3::DependencyKindV3;
use crate::toolchain_lockfile_schema::version::v3::DependencyRoleV3;
use crate::toolchain_lockfile_schema::version::v3::DependencyScopeV3;
use crate::toolchain_lockfile_schema::version::v3::DependencyV3;
use crate::toolchain_lockfile_schema::version::v3::MavenAcquisitionV3;
use crate::toolchain_lockfile_write::write_lockfile_atomically;
use eyre::Context;
use facet::Facet;
use figue as args;
use reqwest::StatusCode;
use reqwest::blocking::Client;
use std::collections::BTreeSet;
use std::path::Path;
use std::path::PathBuf;

#[derive(Facet, Debug)]
pub struct DependencyAddArgs {
    /// Stable logical dependency ID.
    #[facet(args::positional)]
    pub id: String,
    /// Branch selector to update. Must match exactly one worktree.
    #[facet(args::named)]
    pub branch: BranchSelector,
    /// Exact Maven coordinate (`group:artifact:version[:classifier][@extension]`).
    #[facet(args::named)]
    pub maven: String,
    /// Semantic scope. Repeat for every required scope.
    #[facet(args::named)]
    pub(crate) scope: Vec<DependencyScopeV3>,
    /// Configured repository ID. When omitted, each configured repository is tried.
    #[facet(default, args::named)]
    pub repository: Option<String>,
    /// Artifact treatment. Mods default to loader-managed-mod.
    #[facet(default, args::named)]
    pub(crate) artifact_treatment: Option<ArtifactTreatmentV3>,
    /// Human-readable display name.
    #[facet(default, args::named)]
    pub display_name: Option<String>,
    /// Upstream project URL.
    #[facet(default, args::named)]
    pub project_url: Option<String>,
    /// Maintainer notes.
    #[facet(default, args::named)]
    pub notes: Option<String>,
}

impl DependencyAddArgs {
    /// # Errors
    ///
    /// Returns an error when resolution, validation, cache writing, or lockfile writing fails.
    pub fn invoke(
        self,
        cancellation_token: &CancellationToken,
        cache_home: &CacheHome,
    ) -> eyre::Result<()> {
        cancellation_token.bail_if_cancelled()?;
        let inventory = load_inventory(self.branch.clone(), cache_home)?;
        let report = add_dependency(inventory, &self, cancellation_token, &http_fetcher()?)?;
        stdout_line(format!(
            "Added {}/main: {} from {} ({})",
            report.dependency_id, report.coordinate, report.repository_id, report.hash
        ))?;
        Ok(())
    }
}

pub(super) struct DependencyAddReport {
    pub(super) dependency_id: String,
    pub(super) component_id: String,
    pub(super) coordinate: String,
    pub(super) repository_id: String,
    pub(super) hash: ContentHash,
}

struct ResolvedMavenArtifact {
    repository_id: String,
    url: String,
    bytes: Vec<u8>,
}

struct LockedComponentEvidence {
    hash: ContentHash,
    cache_path: PathBuf,
    artifact_id: String,
}

pub(super) trait ArtifactFetcher {
    fn fetch(
        &self,
        url: &str,
        cancellation_token: &CancellationToken,
    ) -> eyre::Result<Option<Vec<u8>>>;
}

pub(super) struct ReqwestFetcher(Client);

pub(super) fn http_fetcher() -> eyre::Result<ReqwestFetcher> {
    let client = Client::builder()
        .user_agent(concat!("sfm-propagate-changes/", env!("CARGO_PKG_VERSION")))
        .build()
        .wrap_err("Failed to create Maven HTTP client")?;
    Ok(ReqwestFetcher(client))
}

impl ArtifactFetcher for ReqwestFetcher {
    fn fetch(
        &self,
        url: &str,
        cancellation_token: &CancellationToken,
    ) -> eyre::Result<Option<Vec<u8>>> {
        cancellation_token.bail_if_cancelled()?;
        let response = self
            .0
            .get(url)
            .send()
            .wrap_err_with(|| format!("Failed to fetch {url}"))?;
        if response.status() == StatusCode::NOT_FOUND {
            return Ok(None);
        }
        let response = response
            .error_for_status()
            .wrap_err_with(|| format!("Maven repository rejected {url}"))?;
        cancellation_token.bail_if_cancelled()?;
        Ok(Some(response.bytes()?.to_vec()))
    }
}

fn add_dependency(
    mut inventory: DependencyInventory,
    args: &DependencyAddArgs,
    cancellation_token: &CancellationToken,
    fetcher: &dyn ArtifactFetcher,
) -> eyre::Result<DependencyAddReport> {
    validate_dependency_id(&args.id)?;
    if inventory
        .lockfile
        .dependencies
        .iter()
        .any(|dependency| dependency.id == args.id)
    {
        eyre::bail!("Dependency '{}' already exists.", args.id);
    }
    inventory.lockfile.dependencies.push(DependencyV3 {
        id: args.id.clone(),
        kind: DependencyKindV3::Mod,
        role: DependencyRoleV3::Integration,
        display_name: args.display_name.clone(),
        project_url: args.project_url.clone(),
        notes: args.notes.clone(),
        components: Vec::new(),
    });
    add_component(inventory, args, "main", cancellation_token, fetcher)
}

pub(super) fn add_component(
    mut inventory: DependencyInventory,
    args: &DependencyAddArgs,
    component_id: &str,
    cancellation_token: &CancellationToken,
    fetcher: &dyn ArtifactFetcher,
) -> eyre::Result<DependencyAddReport> {
    validate_component_id(component_id)?;
    let dependency = inventory
        .lockfile
        .dependencies
        .iter()
        .find(|dependency| dependency.id == args.id)
        .ok_or_else(|| eyre::eyre!("Unknown dependency '{}'.", args.id))?;
    if dependency
        .components
        .iter()
        .any(|component| component.id == component_id)
    {
        eyre::bail!("Component '{}/{}' already exists.", args.id, component_id);
    }
    if args.scope.is_empty() {
        eyre::bail!("At least one --scope is required.");
    }
    let coordinate = MavenCoordinate::parse(&args.maven)?;
    coordinate.require_exact()?;
    if inventory
        .lockfile
        .artifacts
        .iter()
        .any(|artifact| artifact.coordinate.as_deref() == Some(coordinate.canonical.as_str()))
    {
        eyre::bail!("Artifact '{}' is already locked.", coordinate.canonical);
    }

    let resolved = resolve_artifact(
        &inventory,
        &coordinate,
        args.repository.as_deref(),
        cancellation_token,
        fetcher,
    )?;

    let hash = ContentHash::from_bytes(&resolved.bytes, ContentHashAlgorithm::Blake3);
    let portable_cache_path = coordinate.portable_cache_path();
    let cache_path = inventory.local_path(&portable_cache_path);
    write_cache_file_atomically(&cache_path, &resolved.bytes)?;
    let artifact_id = format!(
        "{}-{}",
        portable_id(&coordinate.canonical),
        hash.short_hex(8)
    );
    if inventory
        .lockfile
        .artifacts
        .iter()
        .any(|artifact| artifact.id == artifact_id)
    {
        eyre::bail!("Generated artifact ID '{artifact_id}' already exists.");
    }
    let evidence = LockedComponentEvidence {
        hash,
        cache_path: portable_cache_path,
        artifact_id,
    };
    append_lock_entries(
        &mut inventory,
        args,
        component_id,
        &coordinate,
        &resolved,
        evidence,
    );
    let output = inventory.lockfile.to_canonical_json()?;
    write_lockfile_atomically(
        &inventory.lockfile_path,
        &inventory.original_input,
        output.as_bytes(),
    )?;
    Ok(DependencyAddReport {
        dependency_id: args.id.clone(),
        component_id: component_id.to_owned(),
        coordinate: coordinate.canonical,
        repository_id: resolved.repository_id,
        hash,
    })
}

fn append_lock_entries(
    inventory: &mut DependencyInventory,
    args: &DependencyAddArgs,
    component_id: &str,
    coordinate: &MavenCoordinate,
    resolved: &ResolvedMavenArtifact,
    evidence: LockedComponentEvidence,
) {
    let scopes: Vec<_> = args
        .scope
        .iter()
        .copied()
        .collect::<BTreeSet<_>>()
        .into_iter()
        .collect();
    let purposes = purposes_for_scopes(&scopes);
    let component = DependencyComponentV3 {
        id: component_id.to_owned(),
        declaration: ComponentDeclarationV3 {
            acquisition: ComponentAcquisitionV3::Maven(MavenAcquisitionV3 {
                requested_coordinate: coordinate.canonical.clone(),
                repository_id: resolved.repository_id.clone(),
            }),
            scopes,
            artifact_treatment: args
                .artifact_treatment
                .unwrap_or(ArtifactTreatmentV3::LoaderManagedMod),
            data_run_policy: DataRunPolicyV3::Exclude,
        },
        derived_checks: ComponentDerivedChecksV3 {
            artifact_id: evidence.artifact_id.clone(),
            resolved_coordinate: Some(coordinate.canonical.clone()),
            expected_hash: evidence.hash,
            cache_path: evidence.cache_path.clone(),
        },
        source_providers: Vec::new(),
    };
    inventory
        .lockfile
        .dependencies
        .iter_mut()
        .find(|dependency| dependency.id == args.id)
        .expect("component mutation validates dependency")
        .components
        .push(component);
    inventory.lockfile.artifacts.push(ArtifactV3 {
        id: evidence.artifact_id,
        owner: Some(ArtifactOwnerV3 {
            dependency_id: args.id.clone(),
            component_id: component_id.to_owned(),
        }),
        purposes,
        coordinate: Some(coordinate.canonical.clone()),
        repository_id: Some(resolved.repository_id.clone()),
        url: Some(resolved.url.clone()),
        hash: evidence.hash,
        cache_path: evidence.cache_path,
        provenance: ArtifactProvenanceV3::RemoteMaven,
        weak: None,
    });
}

fn resolve_artifact(
    inventory: &DependencyInventory,
    coordinate: &MavenCoordinate,
    requested_repository: Option<&str>,
    cancellation_token: &CancellationToken,
    fetcher: &dyn ArtifactFetcher,
) -> eyre::Result<ResolvedMavenArtifact> {
    let candidates = repository_candidates(inventory, requested_repository)?;
    let mut attempted = Vec::new();
    for (repository_id, repository_url) in candidates {
        cancellation_token.bail_if_cancelled()?;
        let url = coordinate.url(repository_url);
        attempted.push(url.clone());
        if let Some(bytes) = fetcher.fetch(&url, cancellation_token)? {
            return Ok(ResolvedMavenArtifact {
                repository_id: repository_id.to_owned(),
                url,
                bytes,
            });
        }
    }
    eyre::bail!(
        "Could not resolve '{}'. Tried:\n{}",
        coordinate.canonical,
        attempted.join("\n")
    )
}

fn repository_candidates<'a>(
    inventory: &'a DependencyInventory,
    requested: Option<&str>,
) -> eyre::Result<Vec<(&'a str, &'a str)>> {
    if let Some(requested) = requested {
        let repository = inventory
            .lockfile
            .repositories
            .iter()
            .find(|repository| repository.id == requested)
            .ok_or_else(|| eyre::eyre!("Unknown repository '{requested}'."))?;
        return Ok(vec![(&repository.id, &repository.url)]);
    }
    Ok(inventory
        .lockfile
        .repositories
        .iter()
        .map(|repository| (repository.id.as_str(), repository.url.as_str()))
        .collect())
}

fn write_cache_file_atomically(path: &Path, bytes: &[u8]) -> eyre::Result<()> {
    let parent = path
        .parent()
        .ok_or_else(|| eyre::eyre!("Artifact cache path has no parent: {}", path.display()))?;
    std::fs::create_dir_all(parent)
        .wrap_err_with(|| format!("Failed to create {}", parent.display()))?;
    let mut temporary = tempfile::Builder::new()
        .prefix(".sfm-artifact.")
        .suffix(".tmp")
        .tempfile_in(parent)?;
    std::io::Write::write_all(&mut temporary, bytes)?;
    temporary.as_file_mut().sync_all()?;
    temporary
        .persist(path)
        .map_err(|error| error.error)
        .wrap_err_with(|| format!("Failed to publish {}", path.display()))?;
    Ok(())
}

fn validate_dependency_id(id: &str) -> eyre::Result<()> {
    if id.is_empty()
        || !id
            .bytes()
            .all(|byte| byte.is_ascii_lowercase() || byte.is_ascii_digit() || byte == b'-')
    {
        eyre::bail!(
            "Dependency ID '{id}' must contain only lowercase ASCII letters, digits, and hyphens."
        );
    }
    Ok(())
}

fn validate_component_id(id: &str) -> eyre::Result<()> {
    if id.is_empty()
        || !id
            .bytes()
            .all(|byte| byte.is_ascii_lowercase() || byte.is_ascii_digit() || byte == b'-')
    {
        eyre::bail!(
            "Component ID '{id}' must contain only lowercase ASCII letters, digits, and hyphens."
        );
    }
    Ok(())
}

fn portable_id(value: &str) -> String {
    let mut output = String::new();
    for character in value.chars() {
        if character.is_ascii_alphanumeric() {
            output.push(character.to_ascii_lowercase());
        } else if !output.ends_with('-') {
            output.push('-');
        }
    }
    output.trim_matches('-').to_owned()
}

fn purposes_for_scopes(scopes: &[DependencyScopeV3]) -> Vec<ArtifactPurposeV3> {
    let mut purposes = BTreeSet::new();
    for scope in scopes {
        purposes.insert(match scope {
            DependencyScopeV3::AnnotationProcessor | DependencyScopeV3::Compile => {
                ArtifactPurposeV3::Build
            }
            DependencyScopeV3::Runtime | DependencyScopeV3::Bundle => ArtifactPurposeV3::Runtime,
            DependencyScopeV3::GametestCompile | DependencyScopeV3::GametestRuntime => {
                ArtifactPurposeV3::Gametest
            }
            DependencyScopeV3::TestCompile | DependencyScopeV3::TestRuntime => {
                ArtifactPurposeV3::Test
            }
        });
    }
    purposes.into_iter().collect()
}

struct MavenCoordinate {
    canonical: String,
    group: String,
    artifact: String,
    version: String,
    classifier: Option<String>,
    extension: String,
}

impl MavenCoordinate {
    fn parse(input: &str) -> eyre::Result<Self> {
        let (notation, extension) = input
            .split_once('@')
            .map_or((input, "jar"), |(notation, extension)| {
                (notation, extension)
            });
        let parts: Vec<_> = notation.split(':').collect();
        let (group, artifact, version, classifier) = match parts.as_slice() {
            [group, artifact, version] => (*group, *artifact, *version, None),
            [group, artifact, version, classifier] => {
                (*group, *artifact, *version, Some((*classifier).to_owned()))
            }
            _ => eyre::bail!("Invalid Maven coordinate '{input}'."),
        };
        if [group, artifact, version, extension]
            .iter()
            .any(|part| part.is_empty())
            || classifier.as_deref().is_some_and(str::is_empty)
        {
            eyre::bail!("Invalid Maven coordinate '{input}'.");
        }
        let mut canonical = format!("{group}:{artifact}:{version}");
        if let Some(classifier) = &classifier {
            canonical.push(':');
            canonical.push_str(classifier);
        }
        if extension != "jar" {
            canonical.push('@');
            canonical.push_str(extension);
        }
        Ok(Self {
            canonical,
            group: group.to_owned(),
            artifact: artifact.to_owned(),
            version: version.to_owned(),
            classifier,
            extension: extension.to_owned(),
        })
    }

    fn require_exact(&self) -> eyre::Result<()> {
        let normalized = self.version.to_ascii_lowercase();
        if self.version.contains(['+', '*', '[', ']', '(', ')', ','])
            || matches!(normalized.as_str(), "latest" | "release")
        {
            eyre::bail!(
                "Dynamic Maven version '{}' is not supported; provide an exact version.",
                self.version
            );
        }
        Ok(())
    }

    fn file_name(&self) -> String {
        let classifier = self
            .classifier
            .as_deref()
            .map_or_else(String::new, |classifier| format!("-{classifier}"));
        format!(
            "{}-{}{}.{}",
            self.artifact, self.version, classifier, self.extension
        )
    }

    fn url(&self, repository_url: &str) -> String {
        format!(
            "{}/{}/{}/{}/{}",
            repository_url.trim_end_matches('/'),
            self.group.replace('.', "/"),
            self.artifact,
            self.version,
            self.file_name()
        )
    }

    fn portable_cache_path(&self) -> PathBuf {
        PathBuf::from("$sfm-cache")
            .join("maven")
            .join(self.group.replace('.', "/"))
            .join(&self.artifact)
            .join(&self.version)
            .join(self.file_name())
    }
}

#[cfg(test)]
#[path = "dependency_add_tests.rs"]
mod tests;
