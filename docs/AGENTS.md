# Agent Intelligence & Project Context

Welcome to the Super Factory Manager (SFM) codebase. This document provides the "big picture" for AI agents and developers.

## 🏗️ Project Architecture

```pwsh
1.19.2 on  1.19.2 [$?⇡] 
❯ git worktree list
D:/Repos/Minecraft/SFM/repos2/1.19.2  c3d97684f [1.19.2] # repo root
D:/Repos/Minecraft/SFM/repos2/1.19.4  463f6a815 [1.19.4]
D:/Repos/Minecraft/SFM/repos2/1.20    a0a313ecc [1.20]
D:/Repos/Minecraft/SFM/repos2/1.20.1  cbee989da [1.20.1]
D:/Repos/Minecraft/SFM/repos2/1.20.2  c88aa3c35 [1.20.2]
D:/Repos/Minecraft/SFM/repos2/1.20.3  4ae7f28f3 [1.20.3]
D:/Repos/Minecraft/SFM/repos2/1.20.4  743da7c63 [1.20.4]
D:/Repos/Minecraft/SFM/repos2/1.21.0  d65d77ea5 [1.21.0]
D:/Repos/Minecraft/SFM/repos2/1.21.1  a5b79ec84 [1.21.1]
```

The other branches' AGENTS.md files all point to this one, do not read those other agent files to save us some tokens.


## The Golden Rule

> Work only on the oldest version/branch that needs attention; `sfm-propagate-changes.exe git merge` will handle updating the other branches.

I work out of the 1.19.2 branch most of the time. Do NOT propose changes outside of the branch that's currently being worked on; the `sfm-propagate-changes.exe git merge` command will handle updating the other branches.
Feature worktrees that are not named like Minecraft versions are intentionally skipped by propagation.

When merging, the default behaviour should be "keep existing" and subsequently meticulously graft the essence of the change over, we NEVER want to accidentally clobber changes that were present in the code for supporting the newer MC version. Absolute caution must be exercised when modifying build.gradle especially.

## 🛠️ Gradle Commands

Gradle commands against a specific version should be run from the `platform/minecraft/` directory.

To run a command multiple versions, see `sfm-propagate-changes gradle run --help`.

| Task | Command | Description |
| :--- | :--- | :--- |
| **Build** | `./gradlew build` | Standard build and jar creation. Not used until the end where we produce the jar. |
| **Build** | `./gradlew compileJava compileDatagenJava compileGameTestJava compileTestJava` | Checks for compile errors. |
| **Launch** | `./gradlew runClient_teamy` | Starts Minecraft for testing. My config changes the default window size. |
| **Datagen** | `./gradlew runDatagen` | **Crucial.** Generates recipes, tags, and models. Run after modifying datagen sources. |
| **Game Tests** | `./gradlew runGameTestServer`| Runs in-game tests. |
| **Game Tests** | `./Run-SelectedGameTests.ps1 wither_aggro_*` | Runs only matching SFM game tests on `1.19.2`. |
| **Java Tests** | `./gradlew test`| Runs junit tests. |

There is a `sfm-propagate-changes.exe gradle log tldr [--latest|<path>]` command to summarize the gradle output.

### Running Selected SFM Game Tests

`1.19.2` filters SFM game tests during registration, so you can run a subset of tests without changing vanilla GameTest startup.

- Preferred command: `./Run-SelectedGameTests.ps1 wither_aggro_*`
- Direct Gradle command: `./gradlew --no-daemon runGameTestServer -PsfmGameTestSelection=sfm:wither_aggro_*`
- Unqualified selectors are treated as `sfm:<pattern>`
- `*` matches any number of characters and `?` matches one character
- Multiple selectors can be passed with commas, for example: `sfm:wither_aggro_*,sfm:tough_cable_*`
- On `1.19.2`, Forge GameTest shutdown may still make Gradle report a failure after a successful run; `runGameTest/logs/latest.log` is the authoritative result

## 💻 The CLI

[I wrote a Rust CLI to automate the process of running common commands in each of the worktrees.](../platform/cli/sfm-propagate-changes/src/cli/cli.rs)

[It is installed in my path](../platform/cli/sfm-propagate-changes/install.ps1).

```pwsh
❯ sfm-propagate-changes.exe --help
A tool for propagating git changes across Minecraft version worktrees.
This CLI manages merging changes from older Minecraft version branches
to newer ones in a sequential manner.

USAGE:
    sfm-propagate-changes.exe [OPTIONS] <COMMAND>

OPTIONS:
        --debug
            Enable debug logging, including backtraces on panics.
        --log_filter <STRING>
            Log level filter directive.
        --log_file <PATHBUF>
            Write structured ndjson logs to this file or directory. If a directory is provided,
    -h, --help
            Show help message and exit.
    -V, --version
            Show version and exit.
        --completions <bash,zsh,fish>
            Generate shell completions.

COMMANDS:
    gradle
            Run arbitrary gradle task(s) for each worktree in strict sequence
    check
            Check workspace files for correctness
    client
            Client instance tracking and management commands
    server
            Server instance tracking and management commands
    git
            Git operation commands across all worktrees
    home
            Home directory related commands
    cache
            Cache directory related commands
    curseforge
            CurseForge release and file related commands
    jar
            Jar directory and release artifact related commands
    repo-root
            Repo root related commands

❯ sfm-propagate-changes.exe gradle run --help

Run arbitrary gradle task(s) for each worktree in strict sequence

USAGE:
    sfm-propagate-changes.exe gradle run [OPTIONS] <TASKS>

ARGUMENTS:
        <TASKS>
            Gradle tasks to run (for example: `runData`, `runGameTestServer`, `test`).

OPTIONS:
        --branch <STRING>
            Branch selector for worktrees. Defaults to all worktrees for legacy Gradle orchestration.
        --show-logs
            If set, stream gradle stdout/stderr to the console while tasks run.
        --continue-on-error
            If set, continue with later branches after a task failure.


platform\cli\sfm-propagate-changes on  1.19.2 [$!⇡] is 📦 v0.1.0 via 🦀 v1.92.0 
```

After making changes to rust code, run [`check-all.ps1`](../platform/cli/sfm-propagate-changes/check-all.ps1) to validate formatting and linting and build errors.

---

Substantial progress has been made regarding the CLI.

❯ sfm-propagate-changes.exe help list --short
sfm-propagate-changes.exe gradle run
sfm-propagate-changes.exe gradle log list
sfm-propagate-changes.exe gradle log tldr
sfm-propagate-changes.exe client add
sfm-propagate-changes.exe client remove
sfm-propagate-changes.exe client list
sfm-propagate-changes.exe client set-launcher
sfm-propagate-changes.exe client get-launcher
sfm-propagate-changes.exe client launch
sfm-propagate-changes.exe server add
sfm-propagate-changes.exe server remove
sfm-propagate-changes.exe server list
sfm-propagate-changes.exe server launch
sfm-propagate-changes.exe git add
sfm-propagate-changes.exe git commit
sfm-propagate-changes.exe git merge
sfm-propagate-changes.exe git push
sfm-propagate-changes.exe git status
sfm-propagate-changes.exe git status all
sfm-propagate-changes.exe git status dirty
sfm-propagate-changes.exe git status summary
sfm-propagate-changes.exe git tag
sfm-propagate-changes.exe github release now
sfm-propagate-changes.exe github release amend
sfm-propagate-changes.exe home path
sfm-propagate-changes.exe home open
sfm-propagate-changes.exe cache path
sfm-propagate-changes.exe cache open
sfm-propagate-changes.exe cache clean
sfm-propagate-changes.exe curseforge project default set
sfm-propagate-changes.exe curseforge project default show
sfm-propagate-changes.exe curseforge project file list
sfm-propagate-changes.exe curseforge minecraft version list
sfm-propagate-changes.exe curseforge release check
sfm-propagate-changes.exe curseforge release validate
sfm-propagate-changes.exe curseforge release now
sfm-propagate-changes.exe curseforge release amend
sfm-propagate-changes.exe jdk list
sfm-propagate-changes.exe modrinth release check
sfm-propagate-changes.exe modrinth release validate
sfm-propagate-changes.exe modrinth release now
sfm-propagate-changes.exe modrinth release amend
sfm-propagate-changes.exe jar dir set
sfm-propagate-changes.exe jar dir clean
sfm-propagate-changes.exe jar dir show
sfm-propagate-changes.exe jar dir open
sfm-propagate-changes.exe jar plan
sfm-propagate-changes.exe jar build
sfm-propagate-changes.exe jar compare
sfm-propagate-changes.exe jar audit-artifacts
sfm-propagate-changes.exe jar collect
sfm-propagate-changes.exe jar list
sfm-propagate-changes.exe jar update-clients
sfm-propagate-changes.exe jar update-servers
sfm-propagate-changes.exe run client
sfm-propagate-changes.exe run client-smoke
sfm-propagate-changes.exe run client-puppet
sfm-propagate-changes.exe run server
sfm-propagate-changes.exe run data
sfm-propagate-changes.exe run game-test-server
sfm-propagate-changes.exe run test
sfm-propagate-changes.exe run test list
sfm-propagate-changes.exe repo-root set
sfm-propagate-changes.exe repo-root unset
sfm-propagate-changes.exe repo-root show
sfm-propagate-changes.exe repo-root open

We can now launch the game, game tests, server, etc from our rust CLI.
We should prefer to using the rust cli over running `.\gradlew` commands.



## 📝 Changelog

[I track significant changes using one of the template programs that is visible in-game.](../platform/minecraft/src/main/resources/assets/sfm/template_programs/changelog.sfml)

## 🎪 Commit Messages

Commit messages should contain the worktrees relevant to the work that was done (1.19.2, etc).

Commit messages should contain the platform relevant to the work that was done ("mod" (platform/minecraft; java), cli (platform/cli/sfm-propagate-changes), etc).

Commit messages should contain an emoji.

Changes to the user experience of the mod should be additionally documented in [changelog.sfml](../platform/minecraft/src/main/resources/assets/sfm/template_programs/changelog.sfml)

## Writing Java

When we encounter a part of code that requires a difference across the branches, we should introduce adapter methods annotated with `@MCVersionDependentBehaviour` to minimize and make obvious the surface area where the code is forced to change to accommodate the changes in the modding platform.

There's also [guidance on how to find the source code for Minecraft/Forge/NeoForge/etc](./SOURCE_CODE_NAVIGATION.md).
