@echo off
setlocal enabledelayedexpansion

echo 🧹 Stopping and removing all existing containers...

:: 0. 모든 docker-compose 서비스 중지 (볼륨 유지)
docker-compose -f docker-compose.yml -f docker-compose.db.yml down
if %errorlevel% neq 0 (
    echo [ERROR] 컨테이너 종료 실패
    exit /b %errorlevel%
)

:: 1. DB(MariaDB) 컨테이너만 실행
echo Starting MariaDB only...
docker-compose -f docker-compose.db.yml up -d mariadb
if %errorlevel% neq 0 (
    echo [ERROR] MariaDB 실행 실패
    exit /b %errorlevel%
)

:: 2. Spring Boot 애플리케이션 빌드
echo 🔨 Building Spring Boot app...

cd usermanagement || (
    echo [ERROR] usermanagement 디렉토리 없음
    exit /b 1
)

call gradlew.bat build
if %errorlevel% neq 0 (
    echo [ERROR] Gradle 빌드 실패
    cd ..
    exit /b %errorlevel%
)
cd ..

:: 3. 나머지 서비스 실행 (DB 제외)
echo 🚀 Starting remaining services...
docker-compose -f docker-compose.yml up --build -d
