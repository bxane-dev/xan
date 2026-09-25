@echo off
setlocal EnableExtensions
for %%I in ("%~dp0..\..") do set "REPO_ROOT=%%~fI"
cd /d "%REPO_ROOT%"

title XAN Source Compatibility Updater

echo.
echo ============================================
echo        XAN SOURCE CODE UPDATER
echo ============================================
echo.

where powershell.exe >nul 2>&1
if errorlevel 1 (
    echo ERROR: Windows PowerShell was not found.
    goto :fail
)

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "tools\scripts\update_source.ps1" -Verify
if errorlevel 1 goto :fail

echo.
echo ============================================
echo       SOURCE UPDATE CHECK COMPLETE
echo ============================================
echo.
git diff -- app modules tools
echo.
echo Source migrations are applied and the Kotlin compile check passed.
echo.
pause
exit /b 0

:fail
echo.
echo Source migration or compile check failed.
echo Changes were left in the working tree for inspection.
echo.
pause
exit /b 1
