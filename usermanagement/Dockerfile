# 베이스 이미지
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 결과물 복사 (빌드 후 경로 기준)
COPY build/libs/usermanagement-0.0.1-SNAPSHOT.jar app.jar

# 포트 오픈
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

