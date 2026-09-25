param(
    [Parameter(Mandatory = $true)]
    [string]$Png,
    [string]$RoundPng = $Png
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $Png -PathType Leaf)) {
    throw "Logo file not found: $Png"
}

if ([System.IO.Path]::GetExtension($Png).ToLowerInvariant() -notin @(".png", ".webp")) {
    throw "XAN branding expects a PNG or WebP file."
}

$extension = [System.IO.Path]::GetExtension($Png).ToLowerInvariant()
$destination = Join-Path $PSScriptRoot ("hotswap\android\res\drawable-nodpi\xan_mark" + $extension)
New-Item -ItemType Directory -Force -Path (Split-Path $destination) | Out-Null
foreach ($oldExtension in @(".png", ".webp")) {
    $oldPath = Join-Path $PSScriptRoot ("hotswap\android\res\drawable-nodpi\xan_mark" + $oldExtension)
    if ($oldPath -ne $destination -and (Test-Path -LiteralPath $oldPath)) { Remove-Item -LiteralPath $oldPath -Force }
}
Copy-Item -LiteralPath $Png -Destination $destination -Force
$roundDestination = Join-Path $PSScriptRoot ("hotswap\android\res\drawable-nodpi\xan_round_mark" + $extension)
if (-not (Test-Path -LiteralPath $RoundPng -PathType Leaf)) { throw "Round logo file not found: $RoundPng" }
foreach ($oldExtension in @(".png", ".webp")) {
    $oldPath = Join-Path $PSScriptRoot ("hotswap\android\res\drawable-nodpi\xan_round_mark" + $oldExtension)
    if ($oldPath -ne $roundDestination -and (Test-Path -LiteralPath $oldPath)) { Remove-Item -LiteralPath $oldPath -Force }
}
Copy-Item -LiteralPath $RoundPng -Destination $roundDestination -Force

Write-Host "XAN logo replaced:" -ForegroundColor Green
Write-Host "  $destination"
Write-Host "  $roundDestination"
Write-Host "Rebuild the APK; launcher/install icon, onboarding, artwork fallbacks, notification/TV art and other XAN branding will use it."
