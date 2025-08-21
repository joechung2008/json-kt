# GitHub Copilot Instructions

This repository is a Kotlin multi-module project. To assist developers, Copilot should follow these guidelines:

## Project Structure

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

## Suggestions for Copilot

- Suggest code that matches the existing style and conventions.
- Prefer Kotlin standard library and idioms.
- When generating tests, use JUnit and mock dependencies as needed.
- For CLI code, entry point is `App.kt` in `app/src/main/kotlin/joechungmsft/jsonkt/cli/`.

## Ktor API

The Ktor API server is implemented in the `api` module.

### Starting the API Server

To run the API server locally:

```sh
# Unix/macOS
./gradlew :api:run
# Windows
./gradlew.bat :api:run
```

The server will start at [http://localhost:8080](http://localhost:8080).

### Testing the API with REST Client

You can test API endpoints using the [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) extension for VS Code.

Sample HTTP request files are provided in the `testdata/` directory (e.g., `testdata/all.rest`, `testdata/object.rest`, etc.).

Steps:

1. Install the REST Client extension in VS Code.
2. Open any `.rest` file in `testdata/`.
3. Click "Send Request" above the desired HTTP request to execute it against the running API server.
