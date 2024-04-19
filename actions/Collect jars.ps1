Push-Location ".."
try {
    # Get to repos folder
    $cwd = Get-Location
    $expected = "D:\repos\Minecraft\SFM\repos"
    if (-not $cwd -eq $expected) {
        Write-Host $cwd
        throw "This should be ran from a directory that is a child of D:\repos\Minecraft\SFM"
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
} finally {
    Pop-Location
}