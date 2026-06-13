use super::ArtifactLockEntry;
use super::ArtifactLockfile;
use super::ArtifactPlan;
use super::ArtifactProvenance;
use super::ArtifactSource;
use super::BuildPlan;
use super::ChangedEntry;
use super::DependencyLockEntry;
use super::DependencyPlan;
use super::DependencySource;
use super::ForgeUserdevConfig;
use super::GraphNode;
use super::JarCompareReport;
use super::JavaPlan;
use super::ManifestCompare;
use super::MavenCoordinate;
use super::McpConfigJson;
use super::McpConfigPlan;
use super::MinecraftPlan;
use super::MinecraftVersionJson;
use super::MojangVersionManifest;
use super::NodeStatus;
use super::ParchmentData;
use super::Repository;
use super::compare_version_text;
use super::extract_quoted;
use super::interpolate_properties;
use super::normalize_manifest_bytes;
use super::parse_maven_versions;
use super::rust_output_jar_path;
use std::cmp::Ordering;
use std::collections::BTreeMap;
use std::path::Path;
use std::path::PathBuf;

#[test]
fn parses_classifier_coordinate() {
    let coordinate = MavenCoordinate::parse("net.minecraftforge:forge:1.19.2-43.4.0:userdev")
        .expect("coordinate should parse");
    assert_eq!(
        coordinate.to_string(),
        "net.minecraftforge:forge:1.19.2-43.4.0:userdev"
    );
    assert_eq!(coordinate.file_name(), "forge-1.19.2-43.4.0-userdev.jar");
}

#[test]
fn parses_zip_coordinate() {
    let coordinate =
        MavenCoordinate::parse("de.oceanlabs.mcp:mcp_config:1.19.2-20220805.130853@zip")
            .expect("coordinate should parse");
    assert_eq!(
        coordinate.file_name(),
        "mcp_config-1.19.2-20220805.130853.zip"
    );
}

#[test]
fn extracts_single_or_double_quoted_notation() {
    assert_eq!(
        extract_quoted("fg.deobf('mezz.jei:jei-1.19.2-forge:11.6.0.1018')"),
        Some("mezz.jei:jei-1.19.2-forge:11.6.0.1018".to_string())
    );
    assert_eq!(
        extract_quoted("antlr \"org.antlr:antlr4:4.9.1\""),
        Some("org.antlr:antlr4:4.9.1".to_string())
    );
}

#[test]
fn interpolates_gradle_style_properties() {
    let mut properties = BTreeMap::new();
    properties.insert("minecraft_version".to_string(), "1.19.2".to_string());
    properties.insert("neo_version".to_string(), "43.4.0".to_string());
    assert_eq!(
        interpolate_properties(
            "net.minecraftforge:forge:${minecraft_version}-${neo_version}",
            &properties
        ),
        "net.minecraftforge:forge:1.19.2-43.4.0"
    );
}

#[test]
fn parses_maven_metadata_versions() {
    let versions = parse_maven_versions(
        "<metadata><versioning><versions><version>1.0.0</version><version>1.0.1</version></versions></versioning></metadata>",
    );
    assert_eq!(versions, vec!["1.0.0", "1.0.1"]);
}

#[test]
fn compares_numeric_version_segments() {
    assert_eq!(compare_version_text("1.2.10", "1.2.9"), Ordering::Greater);
}

#[test]
fn rust_output_jar_uses_rust_suffix() {
    let path = rust_output_jar_path(
        Path::new("platform/minecraft"),
        "Super Factory Manager (SFM)",
        "1.19.2",
        "4.33.0",
    );
    assert_eq!(
        path,
        Path::new(
            "platform/minecraft/build/libs/Super Factory Manager (SFM)-MC1.19.2-4.33.0-rust.jar"
        )
    );
}

#[test]
fn manifest_normalization_ignores_timestamp_by_default() {
    let left = b"Manifest-Version: 1.0\r\nImplementation-Timestamp: 2026-01-01T00:00:00-0400\r\nMixinConfigs: sfm.mixins.json\r\n";
    let right = b"Manifest-Version: 1.0\r\nImplementation-Timestamp: 2026-02-02T00:00:00-0400\r\nMixinConfigs: sfm.mixins.json\r\n";
    assert_eq!(
        normalize_manifest_bytes(left, false),
        normalize_manifest_bytes(right, false)
    );
    assert_ne!(
        normalize_manifest_bytes(left, true),
        normalize_manifest_bytes(right, true)
    );
}

#[test]
fn facet_json_roundtrips_artifact_lockfile_and_provenance() {
    let provenance = minimal_provenance();
    let provenance_json =
        facet_json::to_string_pretty(&provenance).expect("provenance should serialize");
    assert!(provenance_json.contains("remote-maven"));
    let parsed_provenance: ArtifactProvenance =
        facet_json::from_str(&provenance_json).expect("provenance should parse");
    assert_eq!(parsed_provenance.sha1, "abc123");

    let lockfile = ArtifactLockfile {
        schema_version: 1,
        minecraft_version: "1.19.2".to_string(),
        maven_cache_dir: PathBuf::from("build/sfm-toolchain/maven"),
        allow_local_artifact_cache: false,
        repositories: vec![Repository {
            name: "Forge".to_string(),
            url: "https://maven.minecraftforge.net".to_string(),
        }],
        dependencies: vec![DependencyLockEntry {
            configuration: "implementation".to_string(),
            notation: "curse.maven:example-1:2.0".to_string(),
            resolved_notation: "curse.maven:example-1:2.0".to_string(),
            source: DependencySource::CurseMaven,
            dynamic_version: false,
            cache_path: PathBuf::from("artifact.jar"),
        }],
        artifacts: vec![ArtifactLockEntry {
            coordinate: Some("g:a:1".to_string()),
            source: ArtifactSource::RemoteMaven,
            repository: Some("Forge".to_string()),
            url: Some("https://example.test/a.jar".to_string()),
            cache_path: PathBuf::from("a.jar"),
            original_path: None,
            sha1: "abc123".to_string(),
        }],
    };
    let json = facet_json::to_string_pretty(&lockfile).expect("lockfile should serialize");
    assert!(json.contains("remote-maven"));
    let parsed: ArtifactLockfile = facet_json::from_str(&json).expect("lockfile should parse");
    assert_eq!(parsed.artifacts[0].source, ArtifactSource::RemoteMaven);
}

#[test]
fn facet_json_serializes_plan_without_embedded_lockfile() {
    let artifact = minimal_artifact();
    let plan = BuildPlan {
        schema_version: 1,
        mode: "plan".to_string(),
        minecraft_version: "1.19.2".to_string(),
        worktree_path: PathBuf::from("D:/Repos/Minecraft/SFM/repos2/1.19.2"),
        minecraft_dir: PathBuf::from("platform/minecraft"),
        gradle_output_jar: PathBuf::from("build/libs/sfm.jar"),
        rust_output_jar: PathBuf::from("build/libs/sfm-rust.jar"),
        cache_dir: PathBuf::from("build/sfm-toolchain"),
        state_dir: PathBuf::from("build/sfm-toolchain/state"),
        maven_cache_dir: PathBuf::from("build/sfm-toolchain/maven"),
        lockfile_path: PathBuf::from("sfm-toolchain.lock.json"),
        lockfile: Some(ArtifactLockfile {
            schema_version: 1,
            minecraft_version: "1.19.2".to_string(),
            maven_cache_dir: PathBuf::from("build/sfm-toolchain/maven"),
            allow_local_artifact_cache: false,
            repositories: Vec::new(),
            dependencies: Vec::new(),
            artifacts: Vec::new(),
        }),
        java: JavaPlan {
            executable: PathBuf::from("java"),
            home: None,
            version_output: "openjdk version \"17\"".to_string(),
            major_version: 17,
        },
        refresh: false,
        allow_local_artifact_cache: false,
        properties: BTreeMap::new(),
        repositories: Vec::new(),
        artifacts: vec![artifact.clone()],
        minecraft: MinecraftPlan {
            version_manifest: artifact.clone(),
            version_json: artifact.clone(),
            client_jar_url: "https://example.test/client.jar".to_string(),
            server_jar_url: "https://example.test/server.jar".to_string(),
            client_mappings_url: "https://example.test/client.txt".to_string(),
            server_mappings_url: "https://example.test/server.txt".to_string(),
            libraries_count: 0,
        },
        forge_userdev: super::ForgeUserdevPlan {
            artifact: artifact.clone(),
            spec: Some(1),
            mcp: Some("de.oceanlabs.mcp:mcp_config:1@zip".to_string()),
            sources: None,
            universal: None,
            binpatcher: None,
            patches: None,
            patches_original_prefix: None,
            patches_modified_prefix: None,
            access_transformers: Vec::new(),
            side_strippers: Vec::new(),
            module_count: 0,
            library_count: 0,
            run_configs: Vec::new(),
        },
        mcp_config: McpConfigPlan {
            artifact,
            joined_steps: vec!["downloadManifest".to_string()],
            function_count: 0,
            data_keys: Vec::new(),
            library_count: 0,
        },
        dependencies: vec![DependencyPlan {
            configuration: "implementation".to_string(),
            notation: "g:a:1".to_string(),
            resolved_notation: "g:a:1".to_string(),
            source: DependencySource::Maven,
            cache_path: PathBuf::from("a.jar"),
            url: None,
            dynamic_version: false,
        }],
        graph: vec![GraphNode {
            id: "resolve-project-config".to_string(),
            kind: "planning".to_string(),
            status: NodeStatus::Ready,
            inputs: Vec::new(),
            outputs: Vec::new(),
            rebuild_reason: "test".to_string(),
        }],
        warnings: Vec::new(),
    };

    let json = facet_json::to_string_pretty(&plan).expect("plan should serialize");
    assert!(json.contains("rust_output_jar"));
    assert!(!json.contains("\"lockfile\""));
}

#[test]
fn facet_json_serializes_compare_report() {
    let report = JarCompareReport {
        gradle_jar: PathBuf::from("gradle.jar"),
        rust_jar: PathBuf::from("rust.jar"),
        strict_manifest: false,
        matches: false,
        total_gradle_entries: 1,
        total_rust_entries: 1,
        compared_entries: 1,
        missing_entries: vec!["a.class".to_string()],
        extra_entries: Vec::new(),
        changed_entries: vec![ChangedEntry {
            path: "b.class".to_string(),
            gradle_sha1: "1".to_string(),
            rust_sha1: "2".to_string(),
        }],
        manifest: ManifestCompare {
            compared: true,
            changed: false,
            ignored_implementation_timestamp: true,
            gradle_sha1: Some("1".to_string()),
            rust_sha1: Some("1".to_string()),
        },
    };
    let json = facet_json::to_string_pretty(&report).expect("report should serialize");
    assert!(json.contains("missing_entries"));
    assert!(json.contains("ignored_implementation_timestamp"));
}

#[test]
fn facet_json_parses_upstream_config_shapes() {
    let manifest: MojangVersionManifest = facet_json::from_str(
        r#"{"versions":[{"id":"1.19.2","url":"https://example.test/1.19.2.json"}]}"#,
    )
    .expect("manifest should parse");
    assert_eq!(manifest.versions[0].id, "1.19.2");

    let version_json: MinecraftVersionJson = facet_json::from_str(
            r#"{
                "downloads": {
                    "client": {"url": "https://example.test/client.jar"},
                    "server": {"url": "https://example.test/server.jar"},
                    "client_mappings": {"url": "https://example.test/client.txt"},
                    "server_mappings": {"url": "https://example.test/server.txt"}
                },
                "assetIndex": {"id": "1.19", "url": "https://example.test/assets.json"},
                "libraries": [{"downloads": {"artifact": {"url": "https://example.test/lib.jar", "path": "g/a/1/a.jar"}}}]
            }"#,
        )
        .expect("version json should parse");
    assert_eq!(version_json.libraries.len(), 1);
    assert_eq!(version_json.asset_index.expect("asset index").id, "1.19");

    let forge: ForgeUserdevConfig = facet_json::from_str(
        r#"{
                "spec": 1,
                "mcp": "de.oceanlabs.mcp:mcp_config:1.19.2@zip",
                "binpatcher": {"version": "net.minecraftforge:binarypatcher:1"},
                "patchesOriginalPrefix": "a/",
                "patchesModifiedPrefix": "b/",
                "ats": ["ats/accesstransformer.cfg"],
                "sass": ["sas.cfg"],
                "modules": ["g:module:1"],
                "libraries": ["g:lib:1"],
                "runs": {
                    "client": {
                        "main": "cpw.mods.bootstraplauncher.BootstrapLauncher",
                        "args": ["--launchTarget", "forgeclientuserdev"],
                        "jvmArgs": ["-Dexample=true"],
                        "env": {"MOD_CLASSES": "{source_roots}"},
                        "props": {"mixin.env.remapRefMap": "true"}
                    }
                }
            }"#,
    )
    .expect("forge userdev config should parse");
    assert_eq!(forge.runs["client"].jvm_args, vec!["-Dexample=true"]);

    let mcp: McpConfigJson = facet_json::from_str(
            r#"{
                "data": {"mappings": "config/joined.tsrg", "inject": "config/inject/", "patches": {"joined": "patches/joined/"}},
                "steps": {"joined": [{"type": "downloadManifest"}, {"name": "extractServer", "type": "bundleExtractJar"}]},
                "functions": {"rename": {"version": "net.minecraftforge:ForgeAutoRenamingTool:0.1.22:all", "args": ["--input", "{input}"], "jvmargs": []}},
                "libraries": {"joined": ["g:lib:1"]}
            }"#,
        )
        .expect("mcp config should parse");
    assert_eq!(mcp.steps.joined[1].name.as_deref(), Some("extractServer"));
    assert_eq!(mcp.functions.len(), 1);

    let parchment: ParchmentData = facet_json::from_str(
            r#"{"classes":[{"name":"net/minecraft/Test","methods":[{"name":"run","descriptor":"()V","parameters":[{"index":1,"name":"level"}]}]}]}"#,
        )
        .expect("parchment should parse");
    assert_eq!(parchment.classes[0].methods[0].parameters[0].name, "level");
}

fn minimal_artifact() -> ArtifactPlan {
    ArtifactPlan {
        id: "artifact".to_string(),
        coordinate: Some("g:a:1".to_string()),
        repository: Some("Forge".to_string()),
        url: Some("https://example.test/a.jar".to_string()),
        cache_path: PathBuf::from("a.jar"),
        sha1: Some("abc123".to_string()),
        downloaded: true,
        required_for: "test".to_string(),
        provenance: minimal_provenance(),
    }
}

fn minimal_provenance() -> ArtifactProvenance {
    ArtifactProvenance {
        schema_version: 1,
        source: ArtifactSource::RemoteMaven,
        coordinate: Some("g:a:1".to_string()),
        repository: Some("Forge".to_string()),
        url: Some("https://example.test/a.jar".to_string()),
        original_path: None,
        sha1: "abc123".to_string(),
    }
}
