#!/bin/bash

set -e  # 오류 발생 시 즉시 종료

echo "🧹 Stopping and removing all existing containers (related to this project)..."

# 0. 모든 docker-compose 서비스 중지 및 삭제 (볼륨은 유지, 필요시 -v 추가)
docker-compose -f docker-compose.yml -f docker-compose.db.yml down

# 1. DB(MariaDB) 컨테이너만 실행
echo "Starting MariaDB only..."
docker-compose -f docker-compose.db.yml up -d mariadb

# 2. Spring Boot 애플리케이션 빌드 (Gradle)
echo "🔨 Building Spring Boot app..."
cd usermanagement || { echo "usermanagement 디렉토리 없음"; exit 1; }
./gradlew build
cd ..

# 3. 나머지 공통 컴포즈 서비스 실행 (DB 제외)
echo "Starting remaining services (Kafka, Redis, Spring Boot app, Mocks, Kafka UI)..."
docker-compose -f docker-compose.yml up --build
