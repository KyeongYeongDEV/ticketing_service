# 1. 빌드 환경 (Amazon Corretto 17 JDK)
# Alpine 대신 호환성이 완벽한 Amazon Linux 기반 이미지를 사용합니다.
FROM amazoncorretto:17 AS builder
WORKDIR /app
COPY . .

# 실행 권한 부여
RUN chmod +x ./gradlew
# 빌드 실행
RUN ./gradlew clean bootJar -x test

# 2. 실행 환경 (Amazon Corretto 17 JDK)
FROM amazoncorretto:17
WORKDIR /app

# 빌드된 JAR 파일을 복사
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]