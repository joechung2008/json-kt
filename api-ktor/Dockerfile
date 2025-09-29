# Dockerfile for api-ktor
FROM gradle:9.0.0-jdk24 AS build
WORKDIR /workspace
RUN mkdir -p /workspace/.gradle
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew shadowJar --no-daemon

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /workspace/api-ktor/build/libs/api-ktor-all.jar ./app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
