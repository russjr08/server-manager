# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13

WORKDIR /app

COPY gradle/ ./gradle
COPY gradlew build.gradle gradle.properties settings.gradle ./

COPY src ./src

RUN ["./gradlew", "shadowJar"]

CMD ["java", "-jar", "./build/libs/Server-Manager-all.jar"]
