use super::*;
use crate::branch_targets::BranchName;
use crate::branch_targets::WorktreePath;
use crate::branch_targets::WorktreeTarget;
use crate::toolchain_lockfile_schema::read_current;

struct FixtureFetcher {
    expected_url: String,
    bytes: Vec<u8>,
}

impl ArtifactFetcher for FixtureFetcher {
    fn fetch(
        &self,
        url: &str,
        _cancellation_token: &CancellationToken,
    ) -> eyre::Result<Option<Vec<u8>>> {
        Ok((url == self.expected_url).then(|| self.bytes.clone()))
    }
}

#[test]
fn add_resolves_into_injected_cache_and_writes_complete_v3_declaration() {
    let directory = tempfile::tempdir().expect("temp directory");
    let cache_home = CacheHome(directory.path().join("isolated-cache"));
    let mut lockfile = read_current(include_str!(
        "../../../../../minecraft/sfm-toolchain.lock.json"
    ))
    .expect("v3 fixture");
    lockfile
        .dependencies
        .retain(|dependency| dependency.id != "cc-tweaked");
    lockfile.artifacts.retain(|artifact| {
        artifact
            .owner
            .as_ref()
            .is_none_or(|owner| owner.dependency_id != "cc-tweaked")
    });
    let input = lockfile.to_canonical_json().expect("fixture JSON");
    let lockfile_path = directory.path().join("sfm-toolchain.lock.json");
    std::fs::write(&lockfile_path, &input).expect("fixture lockfile");
    let inventory = DependencyInventory {
        target: WorktreeTarget {
            branch: BranchName::from("1.19.2"),
            worktree_path: WorktreePath::from(directory.path().to_path_buf()),
            core: true,
            mc_version: None,
        },
        lockfile_path: lockfile_path.clone(),
        cache_home: cache_home.clone(),
        original_input: input,
        lockfile,
    };
    let coordinate = "org.squiddev:cc-tweaked-1.19.2:1.101.3";
    let expected_url = concat!(
        "https://squiddev.cc/maven/org/squiddev/cc-tweaked-1.19.2/1.101.3/",
        "cc-tweaked-1.19.2-1.101.3.jar"
    );
    let bytes = b"isolated CC:Tweaked artifact".to_vec();
    let fetcher = FixtureFetcher {
        expected_url: expected_url.to_owned(),
        bytes: bytes.clone(),
    };

    let report = add_dependency(
        inventory,
        &args(coordinate),
        &CancellationToken::new(),
        &fetcher,
    )
    .expect("dependency add");

    let expected_hash = ContentHash::from_bytes(&bytes, ContentHashAlgorithm::Blake3);
    assert_eq!(report.hash, expected_hash);
    let written =
        read_current(&std::fs::read_to_string(lockfile_path).expect("updated lockfile read"))
            .expect("updated v3 lockfile");
    let dependency = written
        .dependencies
        .iter()
        .find(|dependency| dependency.id == "cc-tweaked")
        .expect("CC:Tweaked declaration");
    assert_eq!(dependency.kind, DependencyKindV3::Mod);
    assert_eq!(dependency.role, DependencyRoleV3::Integration);
    assert_eq!(dependency.components.len(), 1);
    let component = &dependency.components[0];
    assert_eq!(component.id, "main");
    assert_eq!(
        component.declaration.scopes,
        vec![
            DependencyScopeV3::Compile,
            DependencyScopeV3::Runtime,
            DependencyScopeV3::GametestCompile,
            DependencyScopeV3::GametestRuntime,
        ]
    );
    assert_eq!(
        component.declaration.artifact_treatment,
        ArtifactTreatmentV3::LoaderManagedMod
    );
    assert_eq!(
        component.declaration.data_run_policy,
        DataRunPolicyV3::Exclude
    );
    let artifact = written
        .artifacts
        .iter()
        .find(|artifact| artifact.id == component.derived_checks.artifact_id)
        .expect("owned artifact");
    assert_eq!(artifact.repository_id.as_deref(), Some("squiddev"));
    assert_eq!(artifact.url.as_deref(), Some(expected_url));
    assert_eq!(artifact.hash, expected_hash);
    let local_path = cache_home.join("minecraft-toolchain").join(
        component
            .derived_checks
            .cache_path
            .strip_prefix("$sfm-cache")
            .expect("portable path"),
    );
    assert_eq!(std::fs::read(local_path).expect("cached bytes"), bytes);
}

#[test]
fn add_rejects_dynamic_and_malformed_coordinates() {
    let coordinate = MavenCoordinate::parse("example:mod:1.+").expect("coordinate shape");
    assert!(coordinate.require_exact().is_err());
    assert!(MavenCoordinate::parse("example:mod").is_err());
}

fn args(coordinate: &str) -> DependencyAddArgs {
    DependencyAddArgs {
        id: "cc-tweaked".to_owned(),
        branch: BranchSelector::from("1.19.2".to_owned()),
        maven: coordinate.to_owned(),
        scope: vec![
            DependencyScopeV3::Runtime,
            DependencyScopeV3::Compile,
            DependencyScopeV3::GametestRuntime,
            DependencyScopeV3::GametestCompile,
        ],
        repository: Some("squiddev".to_owned()),
        artifact_treatment: None,
        display_name: Some("CC:Tweaked".to_owned()),
        project_url: Some("https://tweaked.cc".to_owned()),
        notes: None,
    }
}
