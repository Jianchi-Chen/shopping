@echo off
echo ====================================
echo Spring Boot Backend Startup
echo ====================================
echo.

REM 检查 Java 版本
echo Checking Java version...
java -version
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)
echo.

REM 检查数据库连接
echo Make sure PostgreSQL is running
echo Database URL: jdbc:postgresql://localhost:5432/postgres
echo.
echo Building application...
call gradlew.bat build -x test
if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

REM 启动应用
echo.
echo Starting application...
echo Application will run on http://localhost:8080/api
echo.
start "Spring Boot Backend" java -jar build\libs\backend-0.0.1-SNAPSHOT.jar

echo.
echo Backend started! Check the new window for logs.
