# Dockerfile for CLI app
FROM gradle:9.0.0-jdk24 AS build
WORKDIR /workspace

# Build as root (default)
RUN mkdir -p /workspace/.gradle
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew shadowJar --no-daemon

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /workspace/app/build/libs/app-all.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
