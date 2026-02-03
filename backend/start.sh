#!/bin/bash

echo "===================================="
echo "Spring Boot Backend Startup"
echo "===================================="
echo ""

# 检查 Java 版本
echo "Checking Java version..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed"
    echo "Please install Java 17 or higher"
    exit 1
fi
java -version
echo ""

# 检查数据库连接
echo "Make sure PostgreSQL is running"
echo "Database URL: jdbc:postgresql://localhost:5432/postgres"
echo ""
echo "Building application..."
./gradlew build -x test
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi

# 启动应用
echo ""
echo "Starting application..."
echo "Application will run on http://localhost:8080/api"
echo ""
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
