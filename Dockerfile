# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13

WORKDIR /app

COPY gradle/ ./gradle
COPY gradlew build.gradle gradle.properties settings.gradle ./

COPY src ./src

CMD ["./gradlew", "shadowJar"]

COPY build/libs .

CMD ["java", "-jar", "Server-Manager-all.jar"]