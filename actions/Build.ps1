Push-Location ".."
try {
    # Get to repos folder
    $cwd = Get-Location
    $expected = "D:\repos\Minecraft\SFM\repos"
    if (-not $cwd -eq $expected) {
        Write-Host $cwd
        throw "This should be ran from a directory that is a child of D:\repos\Minecraft\SFM"
    }

    # Begin time measurement
    $start = Get-Date
    
    # Gather repos
    $repo_clones = Get-ChildItem -Directory | Sort-Object
    <#
❯ $repo_clones

    Directory: D:\Repos\Minecraft\SFM

Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d----          2024-04-14  1:41 PM                SuperFactoryManager 1.19.2
d----          2024-04-14  1:39 PM                SuperFactoryManager 1.19.4
d----          2024-04-14  1:39 PM                SuperFactoryManager 1.20
d----          2024-04-14  1:39 PM                SuperFactoryManager 1.20.1
d----          2024-04-14  1:39 PM                SuperFactoryManager 1.20.2
d----          2024-04-14  1:39 PM                SuperFactoryManager 1.20.3
    #>

    # Perform build
    foreach ($repo in $repo_clones) {
        try {
            Push-Location $repo
            Write-Host "Running runData for $repo"
            .\gradlew.bat runData --no-daemon
            if ($? -eq $false) {
                Write-Warning "runData failed for ${repo}"
            }

            Write-Host "Running build for $repo"
            .\gradlew.bat build --no-daemon
            if ($? -eq $false) {
                throw "Build failed for ${repo}"
            }
        } finally {
            Pop-Location
        }
    }

    # Collect jars
    Write-Host "Collecting jars"
    $outdir = "..\jars"
    New-Item -ItemType Directory -Path $outdir -ErrorAction SilentlyContinue
    $jars = Get-ChildItem -Recurse | Where-Object { $_ -like "*build\libs\*.jar" }
    $jars | ForEach-Object {
        Copy-Item -Path $_.FullName -Destination $outdir
    }
    Invoke-Item $outdir

    # Measure time
    $end = Get-Date
    $elapsed = $end - $start
    Write-Host "All versions built, took $elapsed"
} finally {
    Pop-Location
}