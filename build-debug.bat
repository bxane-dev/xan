@echo off
call "%~dp0tools\windows\BUILD-DEBUG.bat" %*
exit /b %errorlevel%
