# ---- Build Stage ----
FROM gradle:9.0.0-jdk24 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :api-vertx:shadowJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /workspace/api-vertx/build/libs/api-vertx-all.jar ./app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
