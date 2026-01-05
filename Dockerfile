FROM amazoncorretto:17 AS builder
WORKDIR /app
COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean bootJar -x test

FROM amazoncorretto:17
WORKDIR /app

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]