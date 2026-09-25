@echo off
setlocal EnableExtensions
for %%I in ("%~dp0..\..") do set "REPO_ROOT=%%~fI"
cd /d "%REPO_ROOT%"

title XAN Dependency + Source Updater

echo.
echo ============================================
echo      XAN DEPENDENCY + SOURCE UPDATER
echo ============================================
echo.

where powershell.exe >nul 2>&1
if errorlevel 1 (
    echo ERROR: Windows PowerShell was not found.
    goto :fail
)

set "MODE="
if /I "%~1"=="major" (
    set "MODE=-AllowMajor"
    echo Major updates are enabled.
) else (
    echo Safe mode: updates stay on the same major version.
    echo Run UPDATE-DEPENDENCIES.bat major to allow major-version updates.
)
echo.

echo [1/3] Checking dependency versions...
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "tools\scripts\update_dependencies.ps1" %MODE%
if errorlevel 1 goto :fail

echo.
echo [2/3] Applying source compatibility migrations...
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "tools\scripts\update_source.ps1"
if errorlevel 1 goto :fail

echo.
echo [3/3] Compile-checking the updated source...
call gradlew.bat :app:compileUniversalGmsDebugKotlin --no-configuration-cache --console=plain --warning-mode summary
if errorlevel 1 goto :fail

echo.
echo ============================================
echo       UPDATE + SOURCE CHECK COMPLETE
echo ============================================
echo.
git diff -- .
echo.
echo Dependency versions and source migrations are ready for review.
echo The compile check passed.
echo.
pause
exit /b 0

:fail
echo.
echo Update or source compatibility check failed.
echo Changes were left in the working tree so the failure can be inspected.
echo.
pause
exit /b 1
