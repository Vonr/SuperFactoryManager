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

## 🛠️ Gradle Commands

Gradle commands against a specific version should be run from the `platform/minecraft/` directory.

To run a command multiple versions, see `sfm-propagate-changes gradle`.

| Task | Command | Description |
| :--- | :--- | :--- |
| **Build** | `./gradlew build` | Standard build and jar creation. Not used until the end where we produce the jar. |
| **Build** | `./gradlew compileJava compileDatagenJava compileGameTestJava compileTestJava` | Checks for compile errors. |
| **Launch** | `./gradlew runClient_teamy` | Starts Minecraft for testing. My config changes the default window size. |
| **Datagen** | `./gradlew runDatagen` | **Crucial.** Generates recipes, tags, and models. Run after modifying datagen sources. |
| **Game Tests** | `./gradlew runGameTestServer`| Runs in-game tests. |
| **Java Tests** | `./gradlew test`| Runs junit tests. |

## 💻 The CLI

[I wrote a Rust CLI to automate the process of running common commands in each of the worktrees.](../platform/cli/sfm-propagate-changes/src/cli/cli.rs)

[It is installed in my path](../platform/cli/sfm-propagate-changes/install.ps1).

```pwsh
❯ sfm-propagate-changes.exe --help
sfm-propagate-changes.exe 0.1.0

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
    merge
            Propagate changes by merging from older to newer version branches
    gradle
            Run arbitrary gradle task(s) for each worktree in strict sequence
    check
            Check workspace files for correctness
    push
            Push branches (runs `git push` in each worktree)
    home
            Home directory related commands
    cache
            Cache directory related commands
    repo-root
            Repo root related commands
    status
            Show git status for all worktrees

❯ sfm-propagate-changes gradle --help
sfm-propagate-changes.exe gradle

Run arbitrary gradle task(s) for each worktree in strict sequence

USAGE:
    sfm-propagate-changes.exe gradle [OPTIONS] <TASKS>

ARGUMENTS:
        <TASKS>
            Gradle tasks to run (for example: `runData`, `runGameTestServer`, `test`).

OPTIONS:
        --mc <STRING>
            Minecraft version filter expression for branch names (examples: `>=1.21.0`, `<1.20`, `=1.20.4`).
        --hide-logs
            If set, hide stdout of each gradle process while it runs.
```

After making changes to rust code, run [`check-all.ps1`](../platform/cli/sfm-propagate-changes/check-all.ps1) to validate formatting and linting and build errors.

## 📝 Changelog

[I track significant changes using one of the template programs that is visible in-game.](../platform/minecraft/src/main/resources/assets/sfm/template_programs/changelog.sfml)

## 🎪 Commit Messages

Commit messages should contain the worktrees relevant to the work that was done (1.19.2, etc).

[Conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) should be used by agents.

Commit messages should contain the platform relevant to the work that was done ("mod" (platform/minecraft; java), cli (platform/cli/sfm-propagate-changes), etc).

Commit messages should contain an emoji.

## Writing Java

When we encounter a part of code that requires a difference across the branches, we should introduce adapter methods annotated with `@MCVersionDependentBehaviour` to minimize and make obvious the surface area where the code is forced to change to accommodate the changes in the modding platform.