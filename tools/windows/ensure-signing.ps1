$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$store = Join-Path $projectRoot 'app\keystore\release.keystore'
if (Test-Path -LiteralPath $store) { exit 0 }

$keytool = $null
if ($env:JAVA_HOME) {
    $candidate = Join-Path $env:JAVA_HOME 'bin\keytool.exe'
    if (Test-Path -LiteralPath $candidate) { $keytool = $candidate }
}
if (-not $keytool) {
    $candidate = Join-Path $env:ProgramFiles 'Android\Android Studio\jbr\bin\keytool.exe'
    if (Test-Path -LiteralPath $candidate) { $keytool = $candidate }
}
if (-not $keytool) {
    $command = Get-Command keytool.exe -ErrorAction SilentlyContinue
    if ($command) { $keytool = $command.Source }
}
if (-not $keytool) { throw 'keytool was not found. Install Android Studio or JDK 21+.' }

$bytes = New-Object byte[] 24
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
$password = [BitConverter]::ToString($bytes).Replace('-', '')
$env:XAN_SIGNING_PASSWORD = $password
New-Item -ItemType Directory -Force -Path (Split-Path $store) | Out-Null
try {
    & $keytool -genkeypair -noprompt -storetype PKCS12 -keyalg RSA -keysize 3072 -validity 10000 -alias bxane -dname 'CN=bxane, OU=XAN, O=bxane' -keystore $store -storepass:env XAN_SIGNING_PASSWORD -keypass:env XAN_SIGNING_PASSWORD
    if ($LASTEXITCODE -ne 0) { throw 'keytool failed to create the release keystore.' }
    $properties = Join-Path $projectRoot 'local.properties'
    $existing = if (Test-Path -LiteralPath $properties) { Get-Content -LiteralPath $properties | Where-Object { $_ -notmatch '^(STORE_PASSWORD|KEY_ALIAS|KEY_PASSWORD)=' } } else { @() }
    @($existing) + @("STORE_PASSWORD=$password", 'KEY_ALIAS=bxane', "KEY_PASSWORD=$password") |
        Set-Content -LiteralPath $properties -Encoding ASCII
    Write-Host 'Created a bxane release signing key. Back up app\keystore\release.keystore and local.properties securely; future APK updates must use this same key.'
} catch {
    if (Test-Path -LiteralPath $store) { Remove-Item -LiteralPath $store -Force }
    throw
} finally {
    Remove-Item Env:XAN_SIGNING_PASSWORD -ErrorAction SilentlyContinue
}
