use crate::cli::jar::BranchSelector;
use crate::cli::source::SourceArgs;
use crate::cli::source::SourceAuditArgs;
use crate::cli::source::SourceCommand;
use crate::source_audit::SourceLanguage;
use crate::source_audit::SourceLineLimit;
use arbitrary::Arbitrary;
use facet::Facet;
use figue as args;
use figue::TestToArgsConsistencyConfig;
use figue::TestToArgsRoundTrip;
use figue::ToArgs;
use figue::assert_to_args_consistency;
use figue::assert_to_args_roundtrip;
use std::ffi::OsString;

fn args_to_strings(args: Vec<OsString>) -> Vec<String> {
    args.into_iter()
        .map(|arg| arg.to_string_lossy().into_owned())
        .collect()
}

#[test]
fn typed_source_audit_command_roundtrips() {
    let command = SourceArgs {
        command: SourceCommand::Audit(SourceAuditArgs {
            branch: BranchSelector("popular AND >= 1.20.4".to_string()),
            language: vec![SourceLanguage::Rust, SourceLanguage::Java],
            lang: Vec::new(),
            max_lines: SourceLineLimit(1200),
        }),
    };

    let args = args_to_strings(command.to_args().expect("typed command should render"));
    assert_eq!(
        args,
        [
            "audit",
            "--branch",
            "popular AND >= 1.20.4",
            "--language",
            "rust",
            "--language",
            "java",
            "--max-lines",
            "1200",
        ]
    );

    let arg_refs = args.iter().map(String::as_str).collect::<Vec<_>>();
    let parsed = figue::from_slice::<SourceArgs>(&arg_refs)
        .into_result()
        .expect("rendered command should parse")
        .get_silent();
    let SourceCommand::Audit(parsed) = parsed.command;
    assert_eq!(parsed.branch.as_ref(), "popular AND >= 1.20.4");
    assert_eq!(
        parsed.language,
        [SourceLanguage::Rust, SourceLanguage::Java]
    );
    assert_eq!(parsed.max_lines, SourceLineLimit(1200));

    let display = command
        .to_args_string()
        .expect("display command should render")
        .to_string_lossy()
        .into_owned();
    assert!(display.contains("\"popular AND >= 1.20.4\""));

    let full_display = command
        .to_args_string_with_current_exe()
        .expect("full display command should render")
        .to_string_lossy()
        .into_owned();
    assert!(full_display.ends_with(&display));
}

#[derive(Arbitrary, Debug, Facet, PartialEq)]
struct PositionalProbe {
    #[facet(args::positional)]
    value: String,
}

#[test]
fn dash_prefixed_positional_uses_separator_and_roundtrips() {
    let probe = PositionalProbe {
        value: "--not-a-flag".to_string(),
    };
    let args = args_to_strings(probe.to_args().expect("positional should render"));
    assert_eq!(args, ["--", "--not-a-flag"]);

    let arg_refs = args.iter().map(String::as_str).collect::<Vec<_>>();
    let parsed = figue::from_slice::<PositionalProbe>(&arg_refs)
        .into_result()
        .expect("rendered positional should parse")
        .get_silent();
    assert_eq!(parsed, probe);
}

#[test]
fn figue_arbitrary_rendering_helpers_pass() {
    assert_to_args_consistency::<PositionalProbe>(TestToArgsConsistencyConfig {
        success_count: 16,
        max_attempts: 256,
        ..Default::default()
    })
    .expect("typed rendering should be deterministic");

    assert_to_args_roundtrip::<PositionalProbe>(TestToArgsRoundTrip {
        success_count_global: 16,
        max_attempts_global: 256,
        ..Default::default()
    })
    .expect("generated positional values should roundtrip");
}
