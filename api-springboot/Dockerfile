# ---- Build Stage ----
FROM gradle:9.0.0-jdk24 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :api-springboot:bootJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /workspace/api-springboot/build/libs/api-springboot.jar ./app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
