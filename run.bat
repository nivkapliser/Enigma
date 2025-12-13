@echo off
REM Enigma - Exercise 1 run script
REM Requires Java 21

java -version
IF ERRORLEVEL 1 (
    echo Java is not installed or not in PATH.
    pause
    exit /b
)

echo.
echo Starting Enigma Console Application...
echo.

java -jar console.jar

echo.
echo Application finished.
pause
