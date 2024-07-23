$pattern = Read-Host "Enter the pattern to find"
$replace_token = Read-Host "Enter the token to replace with the name of the current record"
$files = rg --files-with-matches $pattern
foreach ($file in $files) {
    Write-Host "Processing $file"
    $content = Get-Content $file -Raw
    $name = $content | rg "public record (\w+)" --replace '$1' --only-matching
    if ([string]::IsNullOrWhiteSpace($name)) {
        Write-Host "No record name found in $file"
        continue
    }
    $content = $content -replace $replace_token,$name
    Set-Content $file $content
}


