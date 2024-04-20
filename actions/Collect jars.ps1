Push-Location ".."
try {
    # Get to repos folder
    $cwd = Get-Location
    $expected = "D:\repos\Minecraft\SFM\repos"
    if (-not $cwd.Path -eq $expected) {
        Write-Host $cwd.Path
        throw "This should be ran from a directory that is a child of D:\repos\Minecraft\SFM"
    }

    # Collect jars
    Write-Host "Collecting jars"
    $outdir = "..\jars"
    New-Item -ItemType Directory -Path $outdir -ErrorAction SilentlyContinue

    # Fetch all jar files in the build/libs directories
    $jars = Get-ChildItem -Recurse | Where-Object { $_ -like "*build\libs\*.jar" }

    # Sort and filter jars by semantic version
    $sortedJars = $jars | ForEach-Object {
        $nameParts = $_.Name -split '-'
        [PSCustomObject]@{
            FullPath = $_.FullName
            Major = $nameParts[1]
            Minor = [int]($nameParts[2].Split('.')[1])
            Patch = $nameParts[3]
        }
    } | Sort-Object -Property Major, Minor -Descending | Group-Object -Property Major | ForEach-Object {
        $_.Group | Sort-Object -Property Minor, Patch -Descending | Select-Object -First 1
    }

    # Copy selected jars to the output directory
    $sortedJars | ForEach-Object {
        Copy-Item -Path $_.FullPath -Destination $outdir
    }

    # Open output directory
    Invoke-Item $outdir
} finally {
    Pop-Location
}
