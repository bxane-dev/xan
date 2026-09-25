param(
    [Parameter(Mandatory = $true)]
    [string]$Png,
    [string]$RoundPng = $Png
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $Png -PathType Leaf)) {
    throw "Logo file not found: $Png"
}

if ([System.IO.Path]::GetExtension($Png).ToLowerInvariant() -ne ".png") {
    throw "XAN branding expects a PNG file."
}

$destination = Join-Path $PSScriptRoot "hotswap\android\res\drawable-nodpi\xan_mark.png"
New-Item -ItemType Directory -Force -Path (Split-Path $destination) | Out-Null
Copy-Item -LiteralPath $Png -Destination $destination -Force
$roundDestination = Join-Path $PSScriptRoot "hotswap\android\res\drawable-nodpi\xan_round_mark.png"
if (-not (Test-Path -LiteralPath $RoundPng -PathType Leaf)) { throw "Round logo file not found: $RoundPng" }
Copy-Item -LiteralPath $RoundPng -Destination $roundDestination -Force

Write-Host "XAN logo replaced:" -ForegroundColor Green
Write-Host "  $destination"
Write-Host "  $roundDestination"
Write-Host "Rebuild the APK; launcher/install icon, onboarding, artwork fallbacks, notification/TV art and other XAN branding will use it."
