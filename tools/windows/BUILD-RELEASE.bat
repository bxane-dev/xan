@echo off
setlocal EnableExtensions DisableDelayedExpansion

for %%I in ("%~dp0..\..") do set "REPO_ROOT=%%~fI"
cd /d "%REPO_ROOT%"

title XAN Release APK Builder

echo.
echo ============================================
echo          XAN RELEASE APK BUILDER
echo ============================================
echo.

if not exist "gradlew.bat" (
    echo ERROR: Could not find the XAN repository root.
    echo Expected gradlew.bat in:
    echo %REPO_ROOT%
    goto :fail
)

powershell -NoProfile -ExecutionPolicy Bypass -File "tools\scripts\check-compose-drawables.ps1"
if errorlevel 1 goto :fail

set "SDK_PATH="
if defined ANDROID_HOME set "SDK_PATH=%ANDROID_HOME%"
if not defined SDK_PATH if defined ANDROID_SDK_ROOT set "SDK_PATH=%ANDROID_SDK_ROOT%"
if not defined SDK_PATH if exist "%LOCALAPPDATA%\Android\Sdk" set "SDK_PATH=%LOCALAPPDATA%\Android\Sdk"

if not defined SDK_PATH (
    echo Android SDK was not detected automatically.
    set /p "SDK_PATH=Android SDK path: "
)

if not defined SDK_PATH (
    echo ERROR: Android SDK path is required.
    goto :fail
)

if not exist "%SDK_PATH%" (
    echo ERROR: Android SDK folder does not exist:
    echo %SDK_PATH%
    goto :fail
)

set "ANDROID_HOME=%SDK_PATH%"
set "ANDROID_SDK_ROOT=%SDK_PATH%"

if not defined JAVA_HOME if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe" set "JAVA_HOME=%ProgramFiles%\Android\Android Studio\jbr"
where java.exe >nul 2>&1
if errorlevel 1 if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Java/JDK was not found. Install Android Studio with its JBR or JDK 21+.
    goto :fail
)

set "VERSION=%~1"
if not defined VERSION for /f "tokens=1,* delims==" %%A in (version.properties) do if /I "%%A"=="XAN_VERSION" set "VERSION=v%%B"

if not defined VERSION (
    echo ERROR: A release version is required.
    goto :fail
)

if /I not "%VERSION:~0,1%"=="v" set "VERSION=v%VERSION%"

set "XAN_VERSION_INPUT=%VERSION%"
powershell -NoProfile -Command "$v=$env:XAN_VERSION_INPUT; if($v -notmatch '^v[0-9]+(?:[.][0-9]+){1,3}$'){exit 1}; $p=$v.Substring(1).Split('.') | ForEach-Object {[int]$_}; while($p.Count -lt 4){$p += 0}; if($p[0] -gt 2099 -or $p[1] -gt 99 -or $p[2] -gt 99 -or $p[3] -gt 99){exit 1}; $code=$p[0]*1000000+$p[1]*10000+$p[2]*100+$p[3]; if($code -lt 1 -or $code -gt 2100000000){exit 1}; exit 0"
if errorlevel 1 (
    echo.
    echo ERROR: Invalid version "%VERSION%".
    echo Use 2 to 4 numeric parts, for example:
    echo   v0.5.2
    echo   v0.1.6.1
    echo   v12.34
    echo Each part after the major version must be between 0 and 99.
    goto :fail
)

if not exist "app\keystore\release.keystore" (
    powershell -NoProfile -ExecutionPolicy Bypass -File "tools\windows\ensure-signing.ps1"
    if errorlevel 1 goto :fail
)

if exist "app\keystore\release.keystore" (
    echo.
    echo Release keystore detected. Checking signing configuration...

    call :has_signing_value STORE_PASSWORD
    if errorlevel 1 (
        call :read_secret "Keystore password" STORE_PASSWORD
        if not defined STORE_PASSWORD (
            echo ERROR: Keystore password is required.
            goto :fail
        )
    )

    call :has_signing_value KEY_ALIAS
    if errorlevel 1 (
        set "KEY_ALIAS=bxane"
        if not defined KEY_ALIAS (
            echo ERROR: Key alias is required.
            goto :fail
        )
    )

    call :has_signing_value KEY_PASSWORD
    if errorlevel 1 (
        call :read_secret "Key password" KEY_PASSWORD
        if not defined KEY_PASSWORD (
            echo ERROR: Key password is required.
            goto :fail
        )
    )

    echo Signing configuration is ready.
) else (
    echo.
    echo No release keystore found.
    echo The build can continue, but Gradle may produce an unsigned APK.
)

set "XAN_RELEASE_TAG=%VERSION%"
set "DIST_DIR=%REPO_ROOT%\dist"
set "OUTPUT=%DIST_DIR%\xan-%VERSION%-release.apk"
set "UNSIGNED_OUTPUT=%DIST_DIR%\xan-%VERSION%-release-unsigned.apk"

echo.
echo Repository: %REPO_ROOT%
echo Android SDK: %ANDROID_HOME%
echo Version: %XAN_RELEASE_TAG%
echo Variant: universalFossRelease
echo Output: %OUTPUT%
echo.

if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
if exist "app\build\outputs\apk" rmdir /S /Q "app\build\outputs\apk"

echo Building release APK...
echo.
call gradlew.bat :app:assembleUniversalFossRelease --no-configuration-cache --console=plain
if errorlevel 1 (
    echo.
    echo ERROR: Gradle release build failed.
    goto :fail
)

set "APK="
for /R "app\build\outputs\apk" %%F in (*.apk) do if not defined APK set "APK=%%~fF"

if not defined APK (
    echo.
    echo ERROR: Gradle finished but no APK was found.
    goto :fail
)

for %%F in ("%APK%") do set "APK_NAME=%%~nxF"

echo %APK_NAME% | findstr /I /C:"unsigned" >nul
if not errorlevel 1 (
    copy /Y "%APK%" "%UNSIGNED_OUTPUT%" >nul
    echo.
    echo ============================================
    echo BUILD FINISHED, BUT APK IS UNSIGNED
    echo ============================================
    echo.
    echo File:
    echo %UNSIGNED_OUTPUT%
    echo.
    echo To create an installable official release APK, add:
    echo   app\keystore\release.keystore
    echo.
    echo And configure these values in local.properties:
    echo   STORE_PASSWORD=...
    echo   KEY_ALIAS=...
    echo   KEY_PASSWORD=...
    echo.
    explorer.exe "%DIST_DIR%"
    pause
    exit /b 0
)

copy /Y "%APK%" "%OUTPUT%" >nul
if errorlevel 1 (
    echo ERROR: Could not copy the built APK to dist.
    goto :fail
)

echo.
echo ============================================
echo RELEASE APK READY
echo ============================================
echo.
echo Version: %VERSION%
echo File:
echo %OUTPUT%
echo.
explorer.exe "%DIST_DIR%"
pause
exit /b 0

:has_signing_value
powershell -NoProfile -Command ^
  "$key='%~1'; $envValue=[Environment]::GetEnvironmentVariable($key); if(-not [string]::IsNullOrWhiteSpace($envValue)){exit 0}; $fileValue=$null; if(Test-Path 'local.properties'){ foreach($line in Get-Content 'local.properties'){ if($line -match ('^\s*' + [regex]::Escape($key) + '\s*=\s*(.*)$')){ $fileValue=$Matches[1]; break } } }; if([string]::IsNullOrWhiteSpace($fileValue)){exit 1}else{exit 0}"
exit /b %errorlevel%

:read_secret
set "%~2="
for /f "usebackq delims=" %%P in (`powershell -NoProfile -Command "$s=Read-Host '%~1' -AsSecureString; $b=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($s); try{$p=[Runtime.InteropServices.Marshal]::PtrToStringBSTR($b); [Console]::Out.WriteLine($p)}finally{[Runtime.InteropServices.Marshal]::ZeroFreeBSTR($b)}"`) do set "%~2=%%P"
exit /b 0

:fail
echo.
echo Build did not complete.
echo.
pause
exit /b 1
