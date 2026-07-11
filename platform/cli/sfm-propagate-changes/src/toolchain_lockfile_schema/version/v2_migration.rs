use crate::jar_build::DependencyLockEntry;
use crate::toolchain_lockfile_schema::version::v2::ArtifactLockfileV2;
use crate::toolchain_lockfile_schema::version::v2::ComponentMigrationHintV2;
use crate::toolchain_lockfile_schema::version::v2::MigrationHintsV2;
use crate::toolchain_lockfile_schema::version::v3::DependencyKindV3;
use facet::Facet;
use std::collections::BTreeMap;
use std::collections::BTreeSet;

#[derive(Clone, Debug, Eq, Facet, PartialEq)]
pub(crate) struct MigrationDiagnostic {
    pub(crate) path: String,
    pub(crate) message: String,
    #[facet(default)]
    pub(crate) legacy_dependency_index: Option<usize>,
    #[facet(default)]
    pub(crate) configuration: Option<String>,
    #[facet(default)]
    pub(crate) coordinate: Option<String>,
    #[facet(default)]
    pub(crate) candidates: Vec<String>,
    pub(crate) remediation: String,
}

impl ArtifactLockfileV2 {
    pub(crate) fn migration_diagnostics(&self) -> Vec<MigrationDiagnostic> {
        let mut diagnostics = Vec::new();
        let Some(hints) = &self.migration_hints else {
            diagnostics.push(diagnostic(
                "migration_hints",
                "v2 lockfile has no migration hints",
                "add migration_hints and rerun dependency migrate --check",
            ));
            return diagnostics;
        };

        require_non_empty(
            hints.minecraft_dependency_id.as_deref(),
            "migration_hints.minecraft_dependency_id",
            "Minecraft dependency ID is missing",
            "set this to the logical dependency whose kind is minecraft",
            &mut diagnostics,
        );
        require_non_empty(
            hints.loader_dependency_id.as_deref(),
            "migration_hints.loader_dependency_id",
            "loader dependency ID is missing",
            "set this to the logical dependency whose kind is loader",
            &mut diagnostics,
        );
        validate_dependencies(hints, &self.dependencies, &mut diagnostics);
        diagnostics
            .sort_by(|left, right| (&left.path, &left.message).cmp(&(&right.path, &right.message)));
        diagnostics
    }
}

fn validate_dependencies(
    hints: &MigrationHintsV2,
    legacy: &[DependencyLockEntry],
    diagnostics: &mut Vec<MigrationDiagnostic>,
) {
    let mut dependency_ids = BTreeSet::new();
    let mut row_owners: BTreeMap<usize, String> = BTreeMap::new();

    for (dependency_index, dependency) in hints.dependencies.iter().enumerate() {
        let path = format!("migration_hints.dependencies[{dependency_index}]");
        if dependency.id.trim().is_empty() {
            diagnostics.push(diagnostic(
                format!("{path}.id"),
                "logical dependency ID is empty",
                "assign a stable logical dependency ID",
            ));
        } else if !dependency_ids.insert(dependency.id.as_str()) {
            diagnostics.push(diagnostic(
                format!("{path}.id"),
                format!("logical dependency ID '{}' is duplicated", dependency.id),
                "give each logical dependency a unique ID",
            ));
        }
        if dependency.kind.is_none() {
            diagnostics.push(missing(
                format!("{path}.kind"),
                "dependency kind is missing",
                &["minecraft", "loader", "mod", "library", "tool"],
            ));
        }
        if dependency.role.is_none() {
            diagnostics.push(missing(
                format!("{path}.role"),
                "dependency role is missing",
                &["platform", "integration", "build", "test", "library"],
            ));
        }
        if dependency.components.is_empty() {
            diagnostics.push(diagnostic(
                format!("{path}.components"),
                "logical dependency has no components",
                "add a component and assign its legacy dependency row indexes",
            ));
        }

        let mut component_ids = BTreeSet::new();
        for (component_index, component) in dependency.components.iter().enumerate() {
            validate_component(
                component,
                &format!("{path}.components[{component_index}]"),
                &dependency.id,
                legacy,
                &mut component_ids,
                &mut row_owners,
                diagnostics,
            );
        }
    }

    validate_platform_reference(
        hints.minecraft_dependency_id.as_deref(),
        DependencyKindV3::Minecraft,
        "migration_hints.minecraft_dependency_id",
        hints,
        diagnostics,
    );
    validate_platform_reference(
        hints.loader_dependency_id.as_deref(),
        DependencyKindV3::Loader,
        "migration_hints.loader_dependency_id",
        hints,
        diagnostics,
    );

    for (index, row) in legacy.iter().enumerate() {
        if !row_owners.contains_key(&index) {
            diagnostics.push(row_diagnostic(
                format!("dependencies[{index}]"),
                "legacy dependency row is not assigned to a migration component",
                index,
                row,
                "add this index to exactly one component's legacy_dependency_indices",
            ));
        }
    }
}

fn validate_component<'a>(
    component: &'a ComponentMigrationHintV2,
    path: &str,
    dependency_id: &str,
    legacy: &[DependencyLockEntry],
    component_ids: &mut BTreeSet<&'a str>,
    row_owners: &mut BTreeMap<usize, String>,
    diagnostics: &mut Vec<MigrationDiagnostic>,
) {
    if component.id.trim().is_empty() {
        diagnostics.push(diagnostic(
            format!("{path}.id"),
            "component ID is empty",
            "assign a stable component ID",
        ));
    } else if !component_ids.insert(component.id.as_str()) {
        diagnostics.push(diagnostic(
            format!("{path}.id"),
            format!("component ID '{}' is duplicated", component.id),
            "give each component a unique ID within its dependency",
        ));
    }

    if component.legacy_dependency_indices.is_empty() {
        diagnostics.push(diagnostic(
            format!("{path}.legacy_dependency_indices"),
            "component owns no legacy dependency rows",
            "add every legacy row represented by this component",
        ));
    }
    for &index in &component.legacy_dependency_indices {
        let Some(row) = legacy.get(index) else {
            diagnostics.push(diagnostic(
                format!("{path}.legacy_dependency_indices"),
                format!("legacy dependency index {index} is out of range"),
                format!("use an index below {}", legacy.len()),
            ));
            continue;
        };
        let owner = format!("{dependency_id}/{}", component.id);
        if let Some(first_owner) = row_owners.insert(index, owner.clone()) {
            diagnostics.push(row_diagnostic(
                format!("{path}.legacy_dependency_indices"),
                format!("row is assigned to both '{first_owner}' and '{owner}'"),
                index,
                row,
                "remove the duplicate index so the row has exactly one owner",
            ));
        }
    }

    match component.scopes.as_deref() {
        None => diagnostics.push(missing(
            format!("{path}.scopes"),
            "semantic scopes are missing",
            &scope_candidates(component, legacy),
        )),
        Some([]) => diagnostics.push(diagnostic(
            format!("{path}.scopes"),
            "semantic scopes are empty",
            "add the scopes represented by the legacy rows",
        )),
        Some(scopes) => {
            let mut unique = BTreeSet::new();
            if scopes.iter().any(|scope| !unique.insert(scope)) {
                diagnostics.push(diagnostic(
                    format!("{path}.scopes"),
                    "semantic scopes contain duplicates",
                    "remove duplicate scopes",
                ));
            }
        }
    }
    if component.artifact_treatment.is_none() {
        diagnostics.push(missing(
            format!("{path}.artifact_treatment"),
            "artifact treatment is missing",
            &["loader-managed-mod", "plain"],
        ));
    }
    if component.data_run_policy.is_none() {
        diagnostics.push(missing(
            format!("{path}.data_run_policy"),
            "data-run policy is missing",
            &["exclude", "include"],
        ));
    }
}

fn validate_platform_reference(
    id: Option<&str>,
    expected_kind: DependencyKindV3,
    path: &str,
    hints: &MigrationHintsV2,
    diagnostics: &mut Vec<MigrationDiagnostic>,
) {
    let Some(id) = id.filter(|id| !id.trim().is_empty()) else {
        return;
    };
    let Some(dependency) = hints
        .dependencies
        .iter()
        .find(|dependency| dependency.id == id)
    else {
        diagnostics.push(diagnostic(
            path,
            format!("platform dependency ID '{id}' does not reference a migration dependency"),
            "use an ID present in migration_hints.dependencies",
        ));
        return;
    };
    if dependency
        .kind
        .as_ref()
        .is_some_and(|kind| kind != &expected_kind)
    {
        diagnostics.push(diagnostic(
            path,
            format!("platform dependency '{id}' has kind {:?}", dependency.kind),
            format!("set its kind to {expected_kind:?} or reference the correct dependency"),
        ));
    }
}

fn scope_candidates(
    component: &ComponentMigrationHintV2,
    legacy: &[DependencyLockEntry],
) -> Vec<&'static str> {
    let mut candidates = BTreeSet::new();
    for row in component
        .legacy_dependency_indices
        .iter()
        .filter_map(|index| legacy.get(*index))
    {
        let configuration = row.configuration.to_ascii_lowercase();
        let candidate = if configuration.contains("gametest") && configuration.contains("runtime") {
            "gametest-runtime"
        } else if configuration.contains("gametest") {
            "gametest-compile"
        } else if configuration.contains("test") && configuration.contains("runtime") {
            "test-runtime"
        } else if configuration.contains("test") {
            "test-compile"
        } else if configuration.contains("runtime") {
            "runtime"
        } else if configuration.contains("compile") || configuration.contains("implementation") {
            "compile"
        } else {
            continue;
        };
        candidates.insert(candidate);
    }
    candidates.into_iter().collect()
}

fn require_non_empty(
    value: Option<&str>,
    path: &str,
    message: &str,
    remediation: &str,
    diagnostics: &mut Vec<MigrationDiagnostic>,
) {
    if value.is_none_or(|value| value.trim().is_empty()) {
        diagnostics.push(diagnostic(path, message, remediation));
    }
}

fn missing(path: String, message: &str, candidates: &[&str]) -> MigrationDiagnostic {
    let mut result = diagnostic(
        path,
        message,
        "populate this field and rerun dependency migrate --check",
    );
    result.candidates = candidates.iter().map(|value| (*value).to_owned()).collect();
    result
}

fn row_diagnostic(
    path: String,
    message: impl Into<String>,
    index: usize,
    row: &DependencyLockEntry,
    remediation: impl Into<String>,
) -> MigrationDiagnostic {
    MigrationDiagnostic {
        path,
        message: message.into(),
        legacy_dependency_index: Some(index),
        configuration: Some(row.configuration.clone()),
        coordinate: Some(row.resolved_notation.clone()),
        candidates: Vec::new(),
        remediation: remediation.into(),
    }
}

fn diagnostic(
    path: impl Into<String>,
    message: impl Into<String>,
    remediation: impl Into<String>,
) -> MigrationDiagnostic {
    MigrationDiagnostic {
        path: path.into(),
        message: message.into(),
        legacy_dependency_index: None,
        configuration: None,
        coordinate: None,
        candidates: Vec::new(),
        remediation: remediation.into(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::toolchain_lockfile_schema::ENGINE_SCHEMA_VERSION;
    use crate::toolchain_lockfile_schema::version::v2::DependencyMigrationHintV2;
    use crate::toolchain_lockfile_schema::version::v3::ArtifactTreatmentV3;
    use crate::toolchain_lockfile_schema::version::v3::DataRunPolicyV3;
    use crate::toolchain_lockfile_schema::version::v3::DependencyRoleV3;
    use crate::toolchain_lockfile_schema::version::v3::DependencyScopeV3;
    use std::path::PathBuf;

    #[test]
    fn absent_hints_produce_one_actionable_root_diagnostic() {
        let lockfile = lockfile(Vec::new(), None);
        let diagnostics = lockfile.migration_diagnostics();

        assert_eq!(diagnostics.len(), 1);
        assert_eq!(diagnostics[0].path, "migration_hints");
        assert!(diagnostics[0].remediation.contains("migrate --check"));
    }

    #[test]
    fn incomplete_hints_report_all_missing_fields_and_row_context() {
        let lockfile = lockfile(
            vec![legacy_row("gametestRuntimeOnly", "example:mod:1.0")],
            Some(MigrationHintsV2 {
                minecraft_dependency_id: None,
                loader_dependency_id: None,
                dependencies: vec![DependencyMigrationHintV2 {
                    id: "example".to_owned(),
                    kind: None,
                    role: None,
                    display_name: None,
                    project_url: None,
                    notes: None,
                    components: vec![ComponentMigrationHintV2 {
                        id: "main".to_owned(),
                        legacy_dependency_indices: vec![0],
                        scopes: None,
                        artifact_treatment: None,
                        data_run_policy: None,
                    }],
                }],
            }),
        );

        let diagnostics = lockfile.migration_diagnostics();
        let paths: BTreeSet<_> = diagnostics.iter().map(|item| item.path.as_str()).collect();
        assert_eq!(diagnostics.len(), 7);
        assert!(paths.contains("migration_hints.minecraft_dependency_id"));
        assert!(paths.contains("migration_hints.loader_dependency_id"));
        assert!(paths.contains("migration_hints.dependencies[0].kind"));
        assert!(paths.contains("migration_hints.dependencies[0].role"));
        assert!(paths.contains("migration_hints.dependencies[0].components[0].scopes"));
        let scopes = diagnostics
            .iter()
            .find(|item| item.path.ends_with(".scopes"))
            .expect("scope diagnostic");
        assert_eq!(scopes.candidates, vec!["gametest-runtime"]);
    }

    #[test]
    fn complete_platform_hints_have_no_diagnostics() {
        let dependencies = vec![
            complete_dependency("minecraft", DependencyKindV3::Minecraft, 0),
            complete_dependency("forge", DependencyKindV3::Loader, 1),
        ];
        let lockfile = lockfile(
            vec![
                legacy_row("implementation", "net.minecraft:minecraft:1.19.2"),
                legacy_row("implementation", "net.minecraftforge:forge:1.19.2-43.4.0"),
            ],
            Some(MigrationHintsV2 {
                minecraft_dependency_id: Some("minecraft".to_owned()),
                loader_dependency_id: Some("forge".to_owned()),
                dependencies,
            }),
        );

        assert_eq!(lockfile.migration_diagnostics(), Vec::new());
    }

    fn complete_dependency(
        id: &str,
        kind: DependencyKindV3,
        legacy_index: usize,
    ) -> DependencyMigrationHintV2 {
        DependencyMigrationHintV2 {
            id: id.to_owned(),
            kind: Some(kind),
            role: Some(DependencyRoleV3::Platform),
            display_name: None,
            project_url: None,
            notes: None,
            components: vec![ComponentMigrationHintV2 {
                id: "main".to_owned(),
                legacy_dependency_indices: vec![legacy_index],
                scopes: Some(vec![DependencyScopeV3::Compile]),
                artifact_treatment: Some(ArtifactTreatmentV3::Plain),
                data_run_policy: Some(DataRunPolicyV3::Exclude),
            }],
        }
    }

    fn lockfile(
        dependencies: Vec<DependencyLockEntry>,
        migration_hints: Option<MigrationHintsV2>,
    ) -> ArtifactLockfileV2 {
        ArtifactLockfileV2 {
            schema_version: ENGINE_SCHEMA_VERSION,
            minecraft_version: "1.19.2".to_owned(),
            maven_cache_dir: PathBuf::from("$sfm-cache/maven"),
            allow_local_artifact_cache: false,
            repositories: Vec::new(),
            dependencies,
            artifacts: Vec::new(),
            migration_hints,
        }
    }

    fn legacy_row(configuration: &str, notation: &str) -> DependencyLockEntry {
        facet_json::from_str(&format!(
            r#"{{
                "configuration": "{configuration}",
                "notation": "{notation}",
                "resolved_notation": "{notation}",
                "source": "Maven",
                "dynamic_version": false,
                "cache_path": "$sfm-cache/dependency.jar"
            }}"#
        ))
        .expect("legacy dependency fixture should parse")
    }
}
