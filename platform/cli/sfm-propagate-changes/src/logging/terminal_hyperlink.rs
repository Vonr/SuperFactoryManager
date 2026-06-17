pub trait TerminalTextExt: std::fmt::Display + Sized {
    fn hyperlink(self, uri: &str) -> String {
        format!("\x1b]8;;{uri}\x1b\\{self}\x1b]8;;\x1b\\")
    }
}

impl<T> TerminalTextExt for T where T: std::fmt::Display + Sized {}

#[cfg(test)]
mod tests {
    #[test]
    fn terminal_hyperlink_wraps_label_with_osc8() {
        assert_eq!(
            "rust".hyperlink("vscode://file/D:/repo/src/main.rs:249:1"),
            "\x1b]8;;vscode://file/D:/repo/src/main.rs:249:1\x1b\\rust\x1b]8;;\x1b\\"
        );
    }

    #[test]
    fn vscode_file_uri_targets_line_and_column() {
        let uri = vscode_file_uri("src/cli/git/status/git_status_cli.rs", 249);

        assert!(uri.starts_with("vscode://file/"));
        assert!(uri.ends_with("/src/cli/git/status/git_status_cli.rs:249:1"));
    }

    #[test]
    fn percent_encode_uri_path_escapes_reserved_path_bytes() {
        assert_eq!(
            percent_encode_uri_path("D:/repo with spaces/src/#file.rs"),
            "D:/repo%20with%20spaces/src/%23file.rs"
        );
    }
}
