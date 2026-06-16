use crate::paths::CACHE_DIR;
use crate::terminal_output::stdout_line;

pub(super) fn invoke() -> eyre::Result<()> { // todo(2026-06-16) shouldn't this file contain a CachePathArgs struct that the invoke fn is a member fn for?
    stdout_line(CACHE_DIR.0.display())
}
