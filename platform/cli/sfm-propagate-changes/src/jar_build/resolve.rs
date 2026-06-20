use super::{
    ArtifactLockEntry, ArtifactLockfile, ArtifactPlan, ArtifactProvenance, ArtifactSource,
    DependencyPlan, DependencySource, MavenCoordinate, Repository, SourceGitProvenance,
    acquire_artifact_path_lock, acquire_artifact_path_read_lock, artifact_provenance,
    compare_version_text, copy_file_to_path_checked_locked, download_text_optional,
    download_to_path_overwrite_locked, existing_file_matches_sha1, file_sha1,
    materialize_source_build, parse_maven_pom_runtime_dependencies, parse_maven_versions,
    prepare_existing_artifact_for_reuse, read_artifact_provenance, remote_exists,
    source_build_checkout_key, source_git_provenance, write_artifact_provenance,
};
use crate::cancellation::CancellationToken;
use crate::logging::set_tracy_thread_name;
use crate::panic::panic_message;
use eyre::Context;
use reqwest::blocking::Client;
use std::fs;
use std::path::{Path, PathBuf};
use std::sync::Arc;

#[derive(Clone, Debug)]
pub(super) struct Resolver {
    pub(super) client: Client,
    cache_dir: PathBuf,
    repositories: Arc<[Repository]>,
    refresh: bool,
    allow_local_artifact_cache: bool,
    artifact_sources: Arc<[PathBuf]>,
    lockfile: Option<Arc<ArtifactLockfile>>,
    materialization_lockfile: Option<Arc<ArtifactLockfile>>,
    pub(super) cancellation_token: CancellationToken,
}

impl Resolver {
    #[expect(
        clippy::too_many_arguments,
        reason = "Resolver construction mirrors the normalized planner state it owns."
    )]
    #[tracing::instrument(
        name = "resolver_new",
        level = "debug",
        skip_all,
        fields(
            cache_dir = %cache_dir.display(),
            repository_count = repositories.len(),
            refresh,
            allow_local_artifact_cache,
            artifact_source_count = artifact_sources.len(),
            has_lockfile = lockfile.is_some(),
        )
    )]
    pub(super) fn new(
        cache_dir: PathBuf,
        repositories: Vec<Repository>,
        refresh: bool,
        allow_local_artifact_cache: bool,
        artifact_sources: Vec<PathBuf>,
        lockfile: Option<ArtifactLockfile>,
        materialization_lockfile: Option<ArtifactLockfile>,
        cancellation_token: CancellationToken,
    ) -> eyre::Result<Self> {
        cancellation_token.bail_if_cancelled()?;
        let client = Client::builder()
            .user_agent("sfm-propagate-changes/no-gradle-toolchain")
            .build()
            .wrap_err("Failed to create HTTP client")?;

        Ok(Self {
            client,
            cache_dir,
            repositories: Arc::from(repositories),
            refresh,
            allow_local_artifact_cache,
            artifact_sources: Arc::from(artifact_sources),
            lockfile: lockfile.map(Arc::new),
            materialization_lockfile: materialization_lockfile.map(Arc::new),
            cancellation_token,
        })
    }

    pub(super) fn resolve_artifacts(
        &self,
        values: impl IntoIterator<Item = (String, MavenCoordinate, String)>,
    ) -> eyre::Result<Vec<ArtifactPlan>> {
        let mut handles = Vec::new();
        for (id, coordinate, required_for) in values {
            let thread_name = format!("resolve-core-{}", id);
            handles.push(
                std::thread::Builder::new()
                    .name(thread_name.clone())
                    .spawn({
                        let resolver = self.clone();
                        let cancellation_token = self.cancellation_token.clone();
                        move || {
                            set_tracy_thread_name(&thread_name);
                            cancellation_token.bail_if_cancelled()?;
                            resolver.resolve_artifact(&id, &coordinate, &required_for)
                        }
                    })?,
            );
            self.cancellation_token.bail_if_cancelled()?;
        }
        let mut rtn = Vec::with_capacity(handles.len());
        for handle in handles {
            match handle.join().map_err(|panic| {
                eyre::eyre!(
                    "Artifact resolution worker panicked: {}",
                    panic_message(&panic)
                )
            })? {
                Ok(x) => rtn.push(x),
                Err(e) => Err(e).wrap_err("Failed to resolve core artifact")?,
            }
        }
        Ok(rtn)
    }

    pub(super) fn resolve_artifact(
        &self,
        id: &str,
        coordinate: &MavenCoordinate,
        required_for: &str,
    ) -> eyre::Result<ArtifactPlan> {
        self.cancellation_token.bail_if_cancelled()?;
        let _span = tracing::debug_span!(
            "resolve_artifact",
            id,
            coordinate = %coordinate,
            required_for,
        )
        .entered();
        let coordinate = {
            let _span = tracing::debug_span!(
                "resolve_artifact_coordinate",
                dynamic_version = coordinate.version.ends_with('+'),
                has_lockfile = self.lockfile.is_some(),
            )
            .entered();
            self.resolve_dynamic_coordinate(coordinate)?
        };
        self.cancellation_token.bail_if_cancelled()?;
        let cache_path = self.cache_path_for(&coordinate);
        let expected_sha1 = {
            let _span = tracing::debug_span!(
                "resolve_artifact_lock_lookup",
                has_lockfile = self.lockfile.is_some()
            )
            .entered();
            self.locked_artifact_sha1(&coordinate)
                .map(ToOwned::to_owned)
        };

        if let Some(artifact) = {
            let _span = tracing::debug_span!(
                "resolve_artifact_valid_cache",
                refresh = self.refresh,
                cache_exists = cache_path.is_file(),
                has_expected_sha1 = expected_sha1.is_some(),
            )
            .entered();
            self.cached_artifact_if_valid(
                id,
                &coordinate,
                &cache_path,
                required_for,
                expected_sha1.as_deref(),
            )?
        } {
            return Ok(artifact);
        }
        self.cancellation_token.bail_if_cancelled()?;

        let _cache_lock = {
            let _span = tracing::debug_span!(
                "resolve_artifact_prepare_cache",
                refresh = self.refresh,
                cache_exists = cache_path.is_file(),
                has_expected_sha1 = expected_sha1.is_some(),
            )
            .entered();
            let cache_lock = acquire_artifact_path_lock(&cache_path)?;
            self.cancellation_token.bail_if_cancelled()?;
            prepare_existing_artifact_for_reuse(&cache_path, expected_sha1.as_deref())?;

            if cache_path.is_file() && !self.refresh {
                let artifact =
                    Self::cached_artifact_plan(id, &coordinate, cache_path, required_for)?;
                self.verify_locked_artifact(&coordinate, &artifact)?;
                tracing::debug!(
                    coordinate = %coordinate,
                    cache_path = %artifact.cache_path.display(),
                    sha1 = artifact.sha1.as_deref(),
                    "artifact cache hit"
                );
                return Ok(artifact);
            }
            cache_lock
        };

        let mut attempted = Vec::new();
        if let Some(artifact) = {
            let _span = tracing::debug_span!(
                "resolve_artifact_remote",
                repository_candidates = self.candidate_repositories(&coordinate).len(),
                has_expected_sha1 = expected_sha1.is_some(),
            )
            .entered();
            self.remote_artifact(
                id,
                &coordinate,
                &cache_path,
                required_for,
                expected_sha1.as_deref(),
                &mut attempted,
            )?
        } {
            return Ok(artifact);
        }
        self.cancellation_token.bail_if_cancelled()?;

        if let Some(artifact) = {
            let _span = tracing::debug_span!(
                "resolve_artifact_explicit_source",
                artifact_source_count = self.artifact_sources.len(),
                has_expected_sha1 = expected_sha1.is_some(),
            )
            .entered();
            self.explicit_artifact_source_fallback(
                id,
                &coordinate,
                cache_path.clone(),
                required_for,
                expected_sha1.as_deref(),
            )?
        } {
            return Ok(artifact);
        }
        self.cancellation_token.bail_if_cancelled()?;

        if let Some(artifact) = {
            let _span = tracing::debug_span!(
                "resolve_artifact_source_build",
                has_materialization_lockfile = self.materialization_lockfile.is_some(),
                has_expected_sha1 = expected_sha1.is_some(),
            )
            .entered();
            self.source_build_fallback(
                id,
                &coordinate,
                cache_path.clone(),
                required_for,
                expected_sha1.as_deref(),
            )?
        } {
            return Ok(artifact);
        }
        self.cancellation_token.bail_if_cancelled()?;

        if let Some(artifact) = {
            let _span = tracing::debug_span!(
                "resolve_artifact_local_cache",
                allow_local_artifact_cache = self.allow_local_artifact_cache,
                has_expected_sha1 = expected_sha1.is_some(),
            )
            .entered();
            self.local_artifact_fallback(
                id,
                &coordinate,
                cache_path,
                required_for,
                expected_sha1.as_deref(),
            )?
        } {
            return Ok(artifact);
        }

        eyre::bail!(
            "Could not resolve artifact {}. Tried:\n{}\nPass --artifact-source <path> to import from an explicit local project/Maven source, or pass --allow-local-artifact-cache to allow bootstrapping from local Maven-created .m2/Gradle caches.",
            coordinate,
            attempted.join("\n")
        );
    }

    fn source_build_fallback(
        &self,
        id: &str,
        coordinate: &MavenCoordinate,
        cache_path: PathBuf,
        required_for: &str,
        expected_sha1: Option<&str>,
    ) -> eyre::Result<Option<ArtifactPlan>> {
        self.cancellation_token.bail_if_cancelled()?;
        let Some(locked) = self.materializable_locked_artifact(coordinate) else {
            return Ok(None);
        };
        let Some(source_git) = &locked.source_git else {
            return Ok(None);
        };
        let Some(source_build) = &locked.source_build else {
            return Ok(None);
        };
        let Some(remote_url) = source_git.remote_url.as_deref() else {
            return Ok(None);
        };

        let checkout_key = source_build_checkout_key(remote_url, &source_git.commit);
        let checkout_dir = self
            .cache_dir
            .parent()
            .unwrap_or(&self.cache_dir)
            .join("source-builds")
            .join(&checkout_key);
        materialize_source_build(
            &self.cancellation_token,
            remote_url,
            &source_git.commit,
            source_build,
            &checkout_dir,
        )?;
        self.cancellation_token.bail_if_cancelled()?;
        let source_output = checkout_dir.join(&source_build.output_path);
        if !source_output.is_file() {
            eyre::bail!(
                "Source build for {} completed but did not produce {}",
                coordinate,
                source_output.display()
            );
        }
        copy_file_to_path_checked_locked(&source_output, &cache_path, expected_sha1)?;
        let sha1 = file_sha1(&cache_path)?;
        let portable_source_root = PathBuf::from("$sfm-cache")
            .join("source-builds")
            .join(checkout_key);
        let provenance = ArtifactProvenance {
            schema_version: 1,
            source: ArtifactSource::SourceBuild,
            coordinate: Some(coordinate.to_string()),
            repository: Some("source-build".to_string()),
            url: Some(remote_url.to_string()),
            original_path: None,
            source_relative_path: Some(source_build.output_path.clone()),
            source_git: Some(SourceGitProvenance {
                root: portable_source_root,
                commit: source_git.commit.clone(),
                branch: source_git.branch.clone(),
                dirty: false,
                remote_url: Some(remote_url.to_string()),
            }),
            source_build: Some(source_build.clone()),
            sha1: sha1.clone(),
        };
        write_artifact_provenance(&cache_path, &provenance)?;
        let artifact = ArtifactPlan {
            id: id.to_string(),
            coordinate: Some(coordinate.to_string()),
            repository: provenance.repository.clone(),
            url: provenance.url.clone(),
            cache_path,
            sha1: Some(sha1),
            downloaded: false,
            required_for: required_for.to_string(),
            provenance,
        };
        self.verify_locked_artifact(coordinate, &artifact)?;
        tracing::info!(
            coordinate = %coordinate,
            cache_path = %artifact.cache_path.display(),
            remote = remote_url,
            commit = source_git.commit,
            tasks = ?source_build.tasks,
            "artifact materialized from source build"
        );
        Ok(Some(artifact))
    }

    fn materializable_locked_artifact(
        &self,
        coordinate: &MavenCoordinate,
    ) -> Option<&ArtifactLockEntry> {
        let coordinate_text = coordinate.to_string();
        self.materialization_lockfile
            .as_ref()?
            .artifacts
            .iter()
            .find(|artifact| {
                artifact.coordinate.as_deref() == Some(coordinate_text.as_str())
                    && artifact.source.can_be_materialized_from_source()
                    && artifact
                        .source_git
                        .as_ref()
                        .is_some_and(|source_git| source_git.remote_url.is_some())
                    && artifact.source_build.is_some()
            })
    }

    fn remote_artifact(
        &self,
        id: &str,
        coordinate: &MavenCoordinate,
        cache_path: &Path,
        required_for: &str,
        expected_sha1: Option<&str>,
        attempted: &mut Vec<String>,
    ) -> eyre::Result<Option<ArtifactPlan>> {
        for repo in self.candidate_repositories(coordinate) {
            let _repo_span = tracing::debug_span!(
                "resolve_artifact_remote_candidate",
                repository = repo.name.as_str(),
                requires_existence_check = coordinate.group != "curse.maven",
            )
            .entered();
            self.cancellation_token.bail_if_cancelled()?;
            let url = Self::artifact_url(repo, coordinate);
            attempted.push(url.clone());
            tracing::debug!(
                coordinate = %coordinate,
                repository = repo.name.as_str(),
                url = url.as_str(),
                "checking artifact remote"
            );
            let download_result = {
                let _span = tracing::debug_span!(
                    "resolve_artifact_remote_download",
                    refresh = self.refresh,
                    has_expected_sha1 = expected_sha1.is_some(),
                )
                .entered();
                if coordinate.group == "curse.maven" {
                    download_to_path_overwrite_locked(
                        &self.cancellation_token,
                        &self.client,
                        &url,
                        cache_path,
                        self.refresh,
                        expected_sha1,
                    )
                } else {
                    if !remote_exists(&self.cancellation_token, &self.client, &url)? {
                        continue;
                    }
                    download_to_path_overwrite_locked(
                        &self.cancellation_token,
                        &self.client,
                        &url,
                        cache_path,
                        self.refresh,
                        expected_sha1,
                    )
                }
            };

            if let Err(error) = download_result {
                if self.cancellation_token.is_cancelled() {
                    return Err(error);
                }
                attempted.push(format!("{url} ({error:#})"));
                continue;
            }

            let artifact = Self::remote_artifact_plan(
                id,
                coordinate,
                repo,
                url,
                cache_path.to_path_buf(),
                required_for,
            )?;
            self.verify_locked_artifact(coordinate, &artifact)?;
            tracing::info!(
                coordinate = %coordinate,
                repository = artifact.repository.as_deref(),
                cache_path = %artifact.cache_path.display(),
                sha1 = artifact.sha1.as_deref(),
                "artifact downloaded"
            );
            return Ok(Some(artifact));
        }
        Ok(None)
    }

    fn explicit_artifact_source_fallback(
        &self,
        id: &str,
        coordinate: &MavenCoordinate,
        cache_path: PathBuf,
        required_for: &str,
        expected_sha1: Option<&str>,
    ) -> eyre::Result<Option<ArtifactPlan>> {
        let Some(local_artifact) =
            find_explicit_source_artifact(coordinate, self.artifact_sources.as_ref())
        else {
            return Ok(None);
        };
        let artifact = Self::local_artifact_plan(
            id,
            coordinate,
            local_artifact,
            cache_path,
            required_for,
            expected_sha1,
        )?;
        self.verify_locked_artifact(coordinate, &artifact)?;
        tracing::info!(
            coordinate = %coordinate,
            cache_path = %artifact.cache_path.display(),
            source = ?artifact.provenance.source,
            original_path = artifact.provenance.original_path.as_ref().map(|path| path.display().to_string()),
            sha1 = artifact.sha1.as_deref(),
            "artifact copied from explicit artifact source"
        );
        Ok(Some(artifact))
    }

    fn local_artifact_fallback(
        &self,
        id: &str,
        coordinate: &MavenCoordinate,
        cache_path: PathBuf,
        required_for: &str,
        expected_sha1: Option<&str>,
    ) -> eyre::Result<Option<ArtifactPlan>> {
        if !self.allow_local_artifact_cache {
            return Ok(None);
        }
        let Some(local_artifact) = find_local_cached_artifact(coordinate) else {
            return Ok(None);
        };
        let artifact = Self::local_artifact_plan(
            id,
            coordinate,
            local_artifact,
            cache_path,
            required_for,
            expected_sha1,
        )?;
        self.verify_locked_artifact(coordinate, &artifact)?;
        tracing::info!(
            coordinate = %coordinate,
            cache_path = %artifact.cache_path.display(),
            source = ?artifact.provenance.source,
            sha1 = artifact.sha1.as_deref(),
            "artifact copied from local cache fallback"
        );
        Ok(Some(artifact))
    }

    fn cached_artifact_if_valid(
        &self,
        id: &str,
        coordinate: &MavenCoordinate,
        cache_path: &Path,
        required_for: &str,
        expected_sha1: Option<&str>,
    ) -> eyre::Result<Option<ArtifactPlan>> {
        if self.refresh || !cache_path.is_file() {
            return Ok(None);
        }

        let _cache_read_lock = acquire_artifact_path_read_lock(cache_path)?;
        let cached_artifact_is_valid = match expected_sha1 {
            Some(expected_sha1) => existing_file_matches_sha1(cache_path, expected_sha1)?,
            None => true,
        };
        if !cached_artifact_is_valid {
            return Ok(None);
        }

        let artifact =
            Self::cached_artifact_plan(id, coordinate, cache_path.to_path_buf(), required_for)?;
        self.verify_locked_artifact(coordinate, &artifact)?;
        tracing::debug!(
            coordinate = %coordinate,
            cache_path = %artifact.cache_path.display(),
            sha1 = artifact.sha1.as_deref(),
            "artifact cache hit"
        );
        Ok(Some(artifact))
    }

    fn locked_artifact_sha1(&self, coordinate: &MavenCoordinate) -> Option<&str> {
        let coordinate_text = coordinate.to_string();
        self.lockfile
            .as_ref()?
            .artifacts
            .iter()
            .find(|entry| entry.coordinate.as_deref() == Some(coordinate_text.as_str()))
            .map(|entry| entry.sha1.as_str())
    }

    fn verify_locked_artifact(
        &self,
        coordinate: &MavenCoordinate,
        artifact: &ArtifactPlan,
    ) -> eyre::Result<()> {
        let Some(lockfile) = &self.lockfile else {
            return Ok(());
        };
        let coordinate_text = coordinate.to_string();
        let Some(locked) = lockfile
            .artifacts
            .iter()
            .find(|entry| entry.coordinate.as_deref() == Some(coordinate_text.as_str()))
        else {
            eyre::bail!(
                "Artifact {} is not present in {}. Run jar build --branch {} --refresh to update the lockfile intentionally.",
                coordinate_text,
                "sfm-toolchain.lock.json",
                lockfile.minecraft_version
            );
        };
        let actual_sha1 = artifact.sha1.as_deref().ok_or_else(|| {
            eyre::eyre!(
                "Resolved artifact {} did not report a SHA-1 for lock verification",
                coordinate_text
            )
        })?;
        if actual_sha1 != locked.sha1 {
            eyre::bail!(
                "Artifact {} resolved with SHA-1 {}, but sfm-toolchain.lock.json requires {}",
                coordinate_text,
                actual_sha1,
                locked.sha1
            );
        }
        Ok(())
    }

    fn cached_artifact_plan(
        id: &str,
        coordinate: &MavenCoordinate,
        cache_path: PathBuf,
        required_for: &str,
    ) -> eyre::Result<ArtifactPlan> {
        let sha1 = file_sha1(&cache_path)?;
        let provenance = read_artifact_provenance(&cache_path)?.unwrap_or_else(|| {
            artifact_provenance(
                ArtifactSource::ExistingSfmCacheUnknown,
                Some(coordinate.to_string()),
                None,
                None,
                None,
                None,
                sha1.clone(),
            )
        });
        Ok(ArtifactPlan {
            id: id.to_string(),
            coordinate: Some(coordinate.to_string()),
            repository: provenance.repository.clone(),
            url: provenance.url.clone(),
            sha1: Some(sha1),
            cache_path,
            downloaded: false,
            required_for: required_for.to_string(),
            provenance,
        })
    }

    fn remote_artifact_plan(
        id: &str,
        coordinate: &MavenCoordinate,
        repo: &Repository,
        url: String,
        cache_path: PathBuf,
        required_for: &str,
    ) -> eyre::Result<ArtifactPlan> {
        let sha1 = file_sha1(&cache_path)?;
        let provenance = artifact_provenance(
            ArtifactSource::RemoteMaven,
            Some(coordinate.to_string()),
            Some(repo.name.clone()),
            Some(url.clone()),
            None,
            None,
            sha1.clone(),
        );
        write_artifact_provenance(&cache_path, &provenance)?;
        Ok(ArtifactPlan {
            id: id.to_string(),
            coordinate: Some(coordinate.to_string()),
            repository: Some(repo.name.clone()),
            url: Some(url),
            sha1: Some(sha1),
            cache_path,
            downloaded: true,
            required_for: required_for.to_string(),
            provenance,
        })
    }

    fn local_artifact_plan(
        id: &str,
        coordinate: &MavenCoordinate,
        local_artifact: LocalCachedArtifact,
        cache_path: PathBuf,
        required_for: &str,
        expected_sha1: Option<&str>,
    ) -> eyre::Result<ArtifactPlan> {
        copy_file_to_path_checked_locked(&local_artifact.path, &cache_path, expected_sha1)?;
        let sha1 = file_sha1(&cache_path)?;
        let source_git = if local_artifact.source == ArtifactSource::ExplicitSource {
            source_git_provenance(&local_artifact.path)
        } else {
            None
        };
        let provenance = artifact_provenance(
            local_artifact.source,
            Some(coordinate.to_string()),
            Some(local_artifact.repository.clone()),
            None,
            Some(local_artifact.path.clone()),
            source_git,
            sha1.clone(),
        );
        write_artifact_provenance(&cache_path, &provenance)?;
        Ok(ArtifactPlan {
            id: id.to_string(),
            coordinate: Some(coordinate.to_string()),
            repository: Some(local_artifact.repository),
            url: Some(local_artifact.path.display().to_string()),
            sha1: Some(sha1),
            cache_path,
            downloaded: false,
            required_for: required_for.to_string(),
            provenance,
        })
    }

    pub(super) fn resolve_dependency(
        &self,
        configuration: &str,
        coordinate: &MavenCoordinate,
    ) -> eyre::Result<DependencyPlan> {
        self.cancellation_token.bail_if_cancelled()?;
        let dynamic_version = coordinate.version.ends_with('+');
        let resolved = self.resolve_dynamic_coordinate(coordinate)?;
        self.cancellation_token.bail_if_cancelled()?;
        let source = if resolved.group == "curse.maven" {
            DependencySource::CurseMaven
        } else {
            DependencySource::Maven
        };
        let artifact = self.resolve_artifact(
            &format!("dependency:{configuration}:{resolved}"),
            &resolved,
            configuration,
        )?;

        Ok(DependencyPlan {
            configuration: configuration.to_string(),
            notation: coordinate.to_string(),
            resolved_notation: resolved.to_string(),
            source,
            cache_path: artifact.cache_path,
            url: artifact.url,
            dynamic_version,
        })
    }

    fn resolve_dynamic_coordinate(
        &self,
        coordinate: &MavenCoordinate,
    ) -> eyre::Result<MavenCoordinate> {
        self.cancellation_token.bail_if_cancelled()?;
        if !coordinate.version.ends_with('+') {
            return Ok(coordinate.clone());
        }

        if let Some(lockfile) = &self.lockfile {
            let notation = coordinate.to_string();
            let Some(locked) = lockfile
                .dependencies
                .iter()
                .find(|dependency| dependency.notation == notation)
            else {
                eyre::bail!(
                    "Dynamic dependency {} is not present in sfm-toolchain.lock.json. Run jar build --branch {} --refresh to update the lockfile intentionally.",
                    notation,
                    lockfile.minecraft_version
                );
            };
            let resolved = MavenCoordinate::parse(&locked.resolved_notation)?;
            if resolved.version.ends_with('+') {
                eyre::bail!(
                    "sfm-toolchain.lock.json resolved {} to dynamic version {}; refresh the lockfile.",
                    notation,
                    locked.resolved_notation
                );
            }
            return Ok(resolved);
        }

        let prefix = coordinate.version.trim_end_matches('+');
        let mut candidates = Vec::new();

        for repo in self.candidate_repositories(coordinate) {
            self.cancellation_token.bail_if_cancelled()?;
            let metadata_url = Self::maven_metadata_url(repo, coordinate);
            let metadata =
                match download_text_optional(&self.cancellation_token, &self.client, &metadata_url)
                {
                    Ok(metadata) => metadata,
                    Err(error) if self.cancellation_token.is_cancelled() => return Err(error),
                    Err(_) => continue,
                };

            self.cancellation_token.bail_if_cancelled()?;

            candidates.extend(
                parse_maven_versions(&metadata)
                    .into_iter()
                    .filter(|version| version.starts_with(prefix)),
            );
        }

        candidates.sort_by(|left, right| compare_version_text(left, right));
        candidates.dedup();

        let Some(version) = candidates.pop() else {
            eyre::bail!(
                "No Maven metadata version matched {} for {}:{}",
                coordinate.version,
                coordinate.group,
                coordinate.artifact
            );
        };

        Ok(MavenCoordinate {
            version,
            ..coordinate.clone()
        })
    }

    fn candidate_repositories(&self, coordinate: &MavenCoordinate) -> Vec<&Repository> {
        let preferred_names: &[&str] = if coordinate.group == "curse.maven" {
            &["CurseMaven"]
        } else if coordinate.group == "com.teamcofh" {
            &["Thermal"]
        } else if coordinate.group == "mezz.jei" {
            &["BlameJared", "JEI"]
        } else if coordinate.group == "org.parchmentmc.data" {
            &["Parchment"]
        } else if coordinate.group == "org.spongepowered" {
            &["Sponge", "Maven Central"]
        } else if coordinate.group == "net.minecraftforge" || coordinate.group == "de.oceanlabs.mcp"
        {
            &["Forge"]
        } else if coordinate.group == "net.neoforged" {
            &["NeoForged"]
        } else if coordinate.group.starts_with("org.")
            || coordinate.group.starts_with("com.github.")
            || coordinate.group.starts_with("junit")
        {
            &["Maven Central"]
        } else {
            &[]
        };

        let mut selected = Vec::new();
        for name in preferred_names {
            if let Some(repo) = self.repositories.iter().find(|repo| repo.name == *name) {
                selected.push(repo);
            }
        }

        if selected.is_empty() {
            selected.extend(self.repositories.iter());
        }

        selected
    }

    pub(super) fn cache_path_for(&self, coordinate: &MavenCoordinate) -> PathBuf {
        maven_cache_path_for(&self.cache_dir, coordinate)
    }

    fn artifact_url(repo: &Repository, coordinate: &MavenCoordinate) -> String {
        format!(
            "{}/{}/{}/{}/{}",
            repo.url.trim_end_matches('/'),
            coordinate.group.replace('.', "/"),
            coordinate.artifact,
            coordinate.version,
            coordinate.file_name()
        )
    }

    fn maven_metadata_url(repo: &Repository, coordinate: &MavenCoordinate) -> String {
        format!(
            "{}/{}/{}/maven-metadata.xml",
            repo.url.trim_end_matches('/'),
            coordinate.group.replace('.', "/"),
            coordinate.artifact
        )
    }

    pub(super) fn resolve_pom_runtime_dependencies(
        &self,
        coordinate: &MavenCoordinate,
    ) -> eyre::Result<Vec<MavenCoordinate>> {
        self.cancellation_token.bail_if_cancelled()?;
        if coordinate.group == "curse.maven"
            || coordinate.classifier.is_some()
            || coordinate.extension != "jar"
        {
            return Ok(Vec::new());
        }

        let pom_coordinate = coordinate.with_extension("pom");
        for repo in self.candidate_repositories(coordinate) {
            self.cancellation_token.bail_if_cancelled()?;
            let url = Self::artifact_url(repo, &pom_coordinate);
            let pom = match download_text_optional(&self.cancellation_token, &self.client, &url) {
                Ok(pom) => pom,
                Err(error) if self.cancellation_token.is_cancelled() => return Err(error),
                Err(_) => continue,
            };
            return Ok(parse_maven_pom_runtime_dependencies(&pom, coordinate));
        }

        Ok(Vec::new())
    }
}

pub(super) fn maven_cache_path_for(cache_dir: &Path, coordinate: &MavenCoordinate) -> PathBuf {
    let mut path = cache_dir.to_path_buf();
    for segment in coordinate.group.split('.') {
        path.push(segment);
    }
    path.join(&coordinate.artifact)
        .join(&coordinate.version)
        .join(coordinate.file_name())
}

#[derive(Debug)]
struct LocalCachedArtifact {
    path: PathBuf,
    source: ArtifactSource,
    repository: String,
}

fn find_explicit_source_artifact(
    coordinate: &MavenCoordinate,
    artifact_sources: &[PathBuf],
) -> Option<LocalCachedArtifact> {
    artifact_sources.iter().find_map(|artifact_source| {
        explicit_artifact_source_candidates(artifact_source, coordinate)
            .into_iter()
            .find(|candidate| candidate.is_file())
            .map(|path| LocalCachedArtifact {
                path,
                source: ArtifactSource::ExplicitSource,
                repository: "explicit-artifact-source".to_string(),
            })
    })
}

fn explicit_artifact_source_candidates(
    artifact_source: &Path,
    coordinate: &MavenCoordinate,
) -> Vec<PathBuf> {
    let file_name = coordinate.file_name();
    if artifact_source.is_file() {
        return artifact_source
            .file_name()
            .and_then(|name| name.to_str())
            .filter(|name| *name == file_name)
            .map_or_else(Vec::new, |_| vec![artifact_source.to_path_buf()]);
    }

    let maven_relative = PathBuf::from(coordinate.group.replace('.', "/"))
        .join(&coordinate.artifact)
        .join(&coordinate.version)
        .join(&file_name);
    vec![
        artifact_source.join(maven_relative),
        artifact_source.join(&file_name),
        artifact_source.join("build").join("libs").join(file_name),
    ]
}

fn find_local_cached_artifact(coordinate: &MavenCoordinate) -> Option<LocalCachedArtifact> {
    let relative = PathBuf::from(coordinate.group.replace('.', "/"))
        .join(&coordinate.artifact)
        .join(&coordinate.version)
        .join(coordinate.file_name());
    if let Some(home) = std::env::var_os("USERPROFILE").or_else(|| std::env::var_os("HOME")) {
        let home = PathBuf::from(home);
        let m2 = home.join(".m2").join("repository").join(&relative);
        if m2.is_file() {
            return Some(LocalCachedArtifact {
                path: m2,
                source: ArtifactSource::LocalM2Cache,
                repository: "local-artifact-cache".to_string(),
            });
        }

        let gradle_module = home
            .join(".gradle")
            .join("caches")
            .join("modules-2")
            .join("files-2.1")
            .join(&coordinate.group)
            .join(&coordinate.artifact)
            .join(&coordinate.version);
        if let Ok(hash_dirs) = fs::read_dir(gradle_module) {
            for hash_dir in hash_dirs.flatten() {
                let candidate = hash_dir.path().join(coordinate.file_name());
                if candidate.is_file() {
                    return Some(LocalCachedArtifact {
                        path: candidate,
                        source: ArtifactSource::LocalGradleModuleCache,
                        repository: "local-artifact-cache".to_string(),
                    });
                }
            }
        }
    }

    None
}
