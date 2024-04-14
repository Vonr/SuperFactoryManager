Push-Location ".."
try {
    $cwd = Get-Location
    $expected = "D:\repos\Minecraft\SFM"
    if (-not $cwd -eq $expected) {
        Write-Host $cwd
        throw "This should be ran from a directory that is a child of D:\repos\Minecraft\SFM"
    }

    $repo_clones = Get-ChildItem -Directory | Sort-Object
    <#
❯ $repo_clones

    Directory: D:\Repos\Minecraft\SFM

Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d----          2024-04-13  7:37 PM                SuperFactoryManager 1.19.2
d----          2024-04-13  7:36 PM                SuperFactoryManager 1.19.4
d----          2024-04-13  6:55 PM                SuperFactoryManager 1.20.2
d----          2024-04-12  8:11 PM                SuperFactoryManager 1.20.3
    #>

    # Check if anything is uncommitted
    foreach ($repo in $repo_clones) {
        try {
            Push-Location $repo.FullName
            $old_git_branch = git rev-parse --abbrev-ref HEAD
            $expected_branch = $repo.Name -split " " | Select-Object -Last 1
            if (-not $old_git_branch -eq $expected_branch) {
                throw "Branch mismatch: dir=$repo expected=$expected_branch got=$old_git_branch"
            }
    
            # New method to check for uncommitted changes
            $modifiedFiles = git diff --name-only
            $stagedFiles = git diff --cached --name-only
            $allChangedFiles = @($modifiedFiles) + @($stagedFiles) | Select-Object -Unique
    
            if ($allChangedFiles.Length -gt 0) {
                if ($allChangedFiles -eq @("mergapalooza.ps1")) {
                    Write-Warning "This script has uncommitted modifications! You have been warned!"
                } else {
                    throw "Uncommitted changes in ${repo}"
                    # $allChangedFiles | ForEach-Object { Write-Host " - $_" }
                    # return 1
                }
            }
        } catch {
            throw "Encountered error validating repo checkout status, stopping: $($_.Exception.Message)"
        } finally {
            Pop-Location
        }
    }
    


    # We want to enumerate each pair of (older, one step newer) directories
    $pairs = $repo_clones | ForEach-Object {
        $older = $_
        $newer = $repo_clones | Where-Object { $_.Name -gt $older.Name } | Select-Object -First 1
        if ($newer) {
            [PSCustomObject]@{
                Older = $older
                Newer = $newer
            }
        }
    }
    <#
❯ $pairs 

Older                                             Newer
-----                                             -----
D:\Repos\Minecraft\SFM\SuperFactoryManager 1.19.2 D:\Repos\Minecraft\SFM\SuperFactoryManager 1.19.4
D:\Repos\Minecraft\SFM\SuperFactoryManager 1.19.4 D:\Repos\Minecraft\SFM\SuperFactoryManager 1.20.2
D:\Repos\Minecraft\SFM\SuperFactoryManager 1.20.2 D:\Repos\Minecraft\SFM\SuperFactoryManager 1.20.3
    #>

    
    foreach ($pair in $pairs) {
        try {
            Push-Location $pair.Newer

            try {
                Push-Location $pair.Older
                $old_git_branch = git rev-parse --abbrev-ref HEAD
            } finally {
                Pop-Location
            }
            
            $new_git_branch = git rev-parse --abbrev-ref HEAD

            if (-not $old_git_branch -or -not $new_git_branch) {
                throw "Failed to determine branch names for $pair"
                break
            }

            Write-Host "`nFetching $old_git_branch to $new_git_branch repository"
            git fetch "$($pair.Older)" "$old_git_branch"
            if ($? -eq $false) {
                throw "Failed to fetch $old_git_branch from $($pair.Older)"
                break
            }
            
            Write-Host "`nMerging $old_git_branch -> $new_git_branch"
            git merge FETCH_HEAD
            if ($? -eq $false) {
                throw "Failed to merge $old_git_branch into $new_git_branch"
                break
            }

            Write-Host "`nPushing $new_git_branch to remote"
            git push origin $new_git_branch
            if ($? -eq $false) {
                throw "Failed to push $new_git_branch to remote"
            }
        } catch {
            throw "Encountered error, stopping: $($_.Exception.Message)"
        } finally {
            Pop-Location
        }
    }
} finally {
    Pop-Location
}