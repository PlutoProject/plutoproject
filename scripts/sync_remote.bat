@echo off
rem Fetch the current branch's remote version
git fetch origin %1
if %errorlevel% neq 0 (
    echo Fetch failed!
    exit /b %errorlevel%
)

rem Rebase onto the fetched branch
git rebase origin/%1
if %errorlevel% neq 0 (
    echo Rebase failed!
    exit /b %errorlevel%
)

echo Rebase completed successfully!