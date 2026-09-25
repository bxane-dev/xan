@echo off
call "%~dp0tools\windows\BUILD-RELEASE.bat" %*
exit /b %errorlevel%
