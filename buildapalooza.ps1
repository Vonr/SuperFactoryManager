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
            .\gradlew.bat runData
            if ($? -eq $false) {
                Write-Warning "runData failed for ${repo}"
            }
            .\gradlew.bat build
            if ($? -eq $false) {
                throw "Build failed for ${repo}"
            }
        } finally {
            Pop-Location
        }
    }

    # Collect jars
    $jars = Get-ChildItem -Recurse | Where-Object { $_ -like "*build\libs\*.jar" }
    $jars | ForEach-Object {
        Copy-Item $_.FullName "SuperFactoryManager 1.19.2\build\libs"
    }
    Invoke-Item "SuperFactoryManager 1.19.2\build\libs"
} finally {
    Pop-Location
}