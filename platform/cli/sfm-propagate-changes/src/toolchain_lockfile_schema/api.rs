use crate::jar_build::ArtifactLockfile;
use crate::toolchain_lockfile_schema::preflight_document::PreflightDocument;
use crate::toolchain_lockfile_schema::version::v1::ArtifactLockfileV1;
use crate::toolchain_lockfile_schema::version::v2::ArtifactLockfileV2;
use crate::toolchain_lockfile_schema::version::v2_migration::MigrationDiagnostic;
use crate::toolchain_lockfile_schema::version::v3::ArtifactLockfileV3;
use crate::toolchain_lockfile_schema::version::v3::SCHEMA_VERSION as V3_SCHEMA_VERSION;
use eyre::Context;

pub(crate) const ENGINE_SCHEMA_VERSION: u32 = 2;
pub(crate) const LATEST_SCHEMA_VERSION: u32 = V3_SCHEMA_VERSION;

pub(crate) enum ToolchainLockfileDocument {
    V1(ArtifactLockfileV1),
    V2 {
        lockfile: ArtifactLockfileV2,
        migration_diagnostics: Vec<MigrationDiagnostic>,
    },
    V3(ArtifactLockfileV3),
}

pub(crate) enum MigrationAnalysis {
    Legacy {
        source_schema_version: u32,
        diagnostics: Vec<MigrationDiagnostic>,
    },
    Current,
}

pub(crate) fn parse_document(input: &str) -> eyre::Result<ToolchainLockfileDocument> {
    let preflight: PreflightDocument = facet_json::from_str(input)
        .wrap_err("failed to parse toolchain lockfile schema preflight")?;

    match preflight.schema_version {
        1 => {
            if input.contains("\"weak\"") {
                eyre::bail!(
                    "toolchain lockfile declares schema_version 1 but contains v2-only field `weak`; update schema_version to {ENGINE_SCHEMA_VERSION}"
                );
            }
            let lockfile: ArtifactLockfileV1 = facet_json::from_str(input)
                .wrap_err("failed to parse toolchain lockfile schema v1")?;
            Ok(ToolchainLockfileDocument::V1(lockfile))
        }
        ENGINE_SCHEMA_VERSION => {
            let lockfile: ArtifactLockfileV2 = facet_json::from_str(input)
                .wrap_err("failed to parse toolchain lockfile schema v2")?;
            let migration_diagnostics = lockfile.migration_diagnostics();
            Ok(ToolchainLockfileDocument::V2 {
                lockfile,
                migration_diagnostics,
            })
        }
        V3_SCHEMA_VERSION => {
            let lockfile: ArtifactLockfileV3 = facet_json::from_str(input)
                .wrap_err("failed to parse toolchain lockfile schema v3")?;
            lockfile.validate()?;
            Ok(ToolchainLockfileDocument::V3(lockfile))
        }
        version if version > LATEST_SCHEMA_VERSION => eyre::bail!(
            "toolchain lockfile schema_version {version} is newer than supported schema_version {LATEST_SCHEMA_VERSION}"
        ),
        version => eyre::bail!(
            "toolchain lockfile schema_version {version} is older than the first supported schema_version 1"
        ),
    }
}

pub(crate) fn upgrade_to_latest(input: &str) -> eyre::Result<ArtifactLockfile> {
    match parse_document(input)? {
        ToolchainLockfileDocument::V1(lockfile) => Ok(lockfile.upgrade().into_latest()),
        ToolchainLockfileDocument::V2 {
            lockfile,
            migration_diagnostics,
        } => {
            let _diagnostic_count = migration_diagnostics.len();
            Ok(lockfile.into_latest())
        }
        ToolchainLockfileDocument::V3(lockfile) => eyre::bail!(
            "schema_version {} is valid but cannot be consumed by the legacy schema_version {ENGINE_SCHEMA_VERSION} engine",
            lockfile.schema_version
        ),
    }
}

pub(crate) fn analyze_migration(input: &str) -> eyre::Result<MigrationAnalysis> {
    match parse_document(input)? {
        ToolchainLockfileDocument::V1(lockfile) => {
            let normalized = lockfile.upgrade();
            Ok(MigrationAnalysis::Legacy {
                source_schema_version: 1,
                diagnostics: normalized.migration_diagnostics(),
            })
        }
        ToolchainLockfileDocument::V2 {
            migration_diagnostics,
            ..
        } => Ok(MigrationAnalysis::Legacy {
            source_schema_version: ENGINE_SCHEMA_VERSION,
            diagnostics: migration_diagnostics,
        }),
        ToolchainLockfileDocument::V3(_) => Ok(MigrationAnalysis::Current),
    }
}
