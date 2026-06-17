use crate::logging::captured_fields::CapturedFields;
use crate::logging::terminal_hyperlink::TerminalTextExt;
use crate::logging::terminal_span_fields::TerminalSpanFields;
use color_eyre::owo_colors::OwoColorize;
use std::fmt::Debug;
use std::io::Write;
use std::path::Path;
use tracing::Event;
use tracing::Id;
use tracing::Level;
use tracing::Subscriber;
use tracing::span::Attributes;
use tracing::span::Record;
use tracing_subscriber::Layer;
use tracing_subscriber::layer::Context;
use tracing_subscriber::registry::LookupSpan;

#[derive(Debug, Default)]
pub struct TerminalEventLayer;

impl<S> Layer<S> for TerminalEventLayer
where
    S: Subscriber + for<'lookup> LookupSpan<'lookup>,
{
    fn on_new_span(&self, attrs: &Attributes<'_>, id: &Id, ctx: Context<'_, S>) {
        let Some(span) = ctx.span(id) else {
            return;
        };

        let mut fields = CapturedFields::default();
        attrs.record(&mut fields);
        span.extensions_mut()
            .insert(TerminalSpanFields::from_captured(&fields));
    }

    fn on_record(&self, id: &Id, values: &Record<'_>, ctx: Context<'_, S>) {
        let Some(span) = ctx.span(id) else {
            return;
        };

        let mut fields = CapturedFields::default();
        values.record(&mut fields);
        let mut extensions = span.extensions_mut();
        if let Some(existing) = extensions.get_mut::<TerminalSpanFields>() {
            existing.update_from_captured(&fields);
        } else {
            extensions.insert(TerminalSpanFields::from_captured(&fields));
        }
    }

    fn on_event(&self, event: &Event<'_>, ctx: Context<'_, S>) {
        let mut fields = CapturedFields::default();
        event.record(&mut fields);

        let decorate = {
            use std::io::IsTerminal as _;
            std::io::stderr().is_terminal() && std::env::var_os("NO_COLOR").is_none()
        };
        let prefix = render_prefix(event, &ctx, &fields, decorate);
        let message = fields.message.as_deref().unwrap_or(event.metadata().name());
        let extra_fields = &fields.extra_fields;
        let mut stderr = std::io::stderr().lock();
        let e: std::io::Result<()> = (move || {
            write!(stderr, "[")?;
            prefix(&mut stderr)?;
            write!(stderr, "] ")?;
            writeln!(stderr, "{message}")?;
            for (name, value) in extra_fields {
                writeln!(stderr, "  {name}={value}")?;
            }
            Ok(())
        })();
        if let Err(error) = e {
            eprintln!("Failed to write log event to terminal: {error}");
        }
    }
}

fn render_prefix<S: Subscriber + for<'lookup> LookupSpan<'lookup>, W: Write>(
    event: &Event<'_>,
    ctx: &Context<'_, S>,
    fields: &CapturedFields,
    decorate: bool,
) -> impl FnOnce(&mut W) -> std::io::Result<()> {
    move |f| {
        let span_fields = TerminalSpanFields::from_scope(&ctx, event);

        // BRANCH
        let branch = fields.branch.as_deref().or(span_fields.branch.as_deref());
        if let Some(branch) = branch {
            f.write_fmt(format_args!("{branch}"))?;
            f.write(" ".as_bytes())?;
        }

        // SOURCE (JAVA, RUST, ETC)
        let source = fields
            .source
            .as_deref()
            .or(span_fields.source.as_deref())
            .unwrap_or("rust");
        let label = {
            let source: &str = &source;
            match source {
                "minecraft" => "mc",
                "java-tool" => "java",
                other => other,
            }
        };
        if label == "rust" && decorate {
            let linked_label = event
                .metadata()
                .file()
                .zip(event.metadata().line())
                .map_or_else(
                    || label.to_string(),
                    |(file, line)| {
                        let uri = vscode_file_uri(file, line);
                        label.hyperlink(&uri)
                    },
                );
            f.write_fmt(format_args!("{}", linked_label.truecolor(255, 165, 0)))?;
        } else {
            f.write_fmt(format_args!("{label}"))?;
        }

        // todo(2026-06-17) probably want to fold SOURCE and PROCESS into one unit, requires identifying where logs with these fields are being emitted
        // PROCESS (CLI, MINECRAFT, ETC)
        let process = fields
            .process
            .as_deref()
            .or(span_fields.process.as_deref())
            .unwrap_or("cli");
        let should_show_process = !process.is_empty()
            && !matches!(
                (source, process),
                ("rust", "cli") | ("minecraft", "minecraft")
            );
        if should_show_process {
            f.write(" ".as_bytes())?;
            f.write_fmt(format_args!("{}", (process.to_string())))?;
        }

        // STREAM (STDOUT, STDERR, ETC)
        let stream = fields
            .stream
            .as_deref()
            .or(span_fields.stream.as_deref())
            .unwrap_or("stdout");
        if stream == "stderr" {
            f.write(" ".as_bytes())?;
            f.write_fmt(format_args!("{}", ("stderr".to_string())))?;
        }

        // LEVEL (INFO, DEBUG, ETC)
        let level = event.metadata().level();
        match *level {
            Level::TRACE => {
                f.write(" ".as_bytes())?;
                f.write_fmt(format_args!("{}", (level.as_str().purple().to_string())))?
            }
            Level::DEBUG => {
                f.write(" ".as_bytes())?;
                f.write_fmt(format_args!("{}", (level.as_str().blue().to_string())))?
            }
            Level::INFO => {}
            Level::WARN => {
                f.write(" ".as_bytes())?;
                f.write_fmt(format_args!("{}", (level.as_str().yellow().to_string())))?
            }
            Level::ERROR => {
                f.write(" ".as_bytes())?;
                f.write_fmt(format_args!("{}", (level.as_str().red().to_string())))?
            }
        };
        Ok(())
    }
}

fn vscode_file_uri(file: &str, line: u32) -> String {
    let path = Path::new(file);
    let path = if path.is_absolute() {
        path.to_path_buf()
    } else {
        Path::new(env!("CARGO_MANIFEST_DIR")).join(path)
    };
    let path = percent_encode_uri_path(&path.to_string_lossy().replace('\\', "/"));
    format!("vscode://file/{path}:{line}:1")
}

fn percent_encode_uri_path(path: &str) -> String {
    let mut output = String::with_capacity(path.len());
    for byte in path.bytes() {
        match byte {
            b'A'..=b'Z' | b'a'..=b'z' | b'0'..=b'9' | b'/' | b':' | b'-' | b'_' | b'.' | b'~' => {
                output.push(char::from(byte))
            }
            byte => {
                const HEX: &[u8; 16] = b"0123456789ABCDEF";
                output.push('%');
                output.push(char::from(HEX[usize::from(byte >> 4)]));
                output.push(char::from(HEX[usize::from(byte & 0x0F)]));
            }
        }
    }
    output
}
