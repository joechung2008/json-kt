# GitHub Copilot Instructions

This repository is a Kotlin multi-module project. To assist developers, Copilot should follow these guidelines:

## Project Structure

- `api-ktor/`: Ktor API application module.
- `api-springboot/`: Spring Boot API application module.
- `api-vertx/`: Vert.x API application module.
- `app/`: Main application module.
- `json/`: Shared JSON utilities and models.
- `buildSrc/`: Gradle build logic and conventions.
- Source code is under `src/main/kotlin/joechungmsft/jsonkt/` in each module.

## Coding Conventions

- Use idiomatic Kotlin (prefer `val` over `var`, use data classes, extension functions).
- Organize code by feature and responsibility.
- Name files and classes clearly (e.g., `Json.kt` for JSON logic).
- Write concise, descriptive KDoc comments for public APIs.

## Best Practices

- Favor immutability and pure functions.
- Use sealed classes for type hierarchies.
- Handle errors with exceptions or sealed result types.
- Write unit tests in `src/test/kotlin/`.

## Gradle

- Use Kotlin DSL for build scripts.
- Dependencies are managed in `gradle/libs.versions.toml`.

## API Information

All API modules provide the same JSON parsing functionality:

- **Endpoint**: `POST /api/v1/parse`
- **Input**: JSON string in request body (content-type: text/plain)
- **Output**: Formatted JSON string or error response (content-type: application/json)
- **Port**: All APIs run on port 8000
- **Functionality**: Parse JSON using the shared `json` module and return formatted result

## Suggestions for Copilot

- Suggest code that matches the existing style and conventions.
- Prefer Kotlin standard library and idioms.
- When generating tests, use JUnit and mock dependencies as needed.
- For CLI code, entry point is `App.kt` in `app/src/main/kotlin/joechungmsft/jsonkt/cli/`.
- For Ktor API code, entry point is `Application.kt` in `api-ktor/src/main/kotlin/joechungmsft/api/`.
- For Spring Boot API code, entry point is `Application.kt` in `api-springboot/src/main/kotlin/joechungmsft/api/`.
- For Vert.x API code, entry point is `Application.kt` in `api-vertx/src/main/kotlin/joechungmsft/api/`.
