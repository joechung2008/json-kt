# json-kt

JSON Parser ported from TypeScript to Kotlin 2.2.0, featuring a CLI application and a parser library.

## Build and Test Instructions

### Prerequisites

- Java 17+
- Gradle (wrapper included)

### Build Kotlin code

```sh
# Unix/macOS
./gradlew build
# Windows
./gradlew.bat build
```

## Formatting and Linting Kotlin Code

This project uses the `org.jlleitschuh.gradle.ktlint` plugin, which is a Gradle wrapper that runs the `ktlint` jar and integrates it into Gradle tasks.

### Lint Kotlin Code

```sh
# Unix/macOS
./gradlew ktlintCheck
```

```sh
# Windows
./gradlew.bat ktlintCheck
```

### Autoformat Kotlin Code

```sh
# Unix/macOS
./gradlew ktlintFormat
```

```sh
# Windows
./gradlew.bat ktlintFormat
```

### Test Kotlin Code

```sh
# Unix/macOS
./gradlew test
```

```sh
# Windows
./gradlew.bat test
```

## Running the CLI

```sh
# Unix/macOS
./gradlew :app:run
```

```sh
# Windows
./gradlew.bat :app:run
```

## Running the Ktor API

The Ktor API server is located in the `api` module.

To start the API server:

```sh
# Unix/macOS
./gradlew :api:run
# Windows
./gradlew.bat :api:run
```

By default, the server runs on [http://localhost:8080](http://localhost:8080).

## Testing the API with REST Client

You can test the API endpoints using the [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) extension for VS Code.

Sample HTTP request files are provided in the `testdata/` directory (e.g., `testdata/all.rest`, `testdata/object.rest`, etc.).

To use:

1. Install the REST Client extension in VS Code.
2. Open any `.rest` file in `testdata/`.
3. Click "Send Request" above the desired HTTP request to execute it against the running API server.

## Notes

- The CLI entry point is in `app/src/main/kotlin/joechungmsft/jsonkt/cli/App.kt`.
- The parser library code is in `json/src/main/kotlin/joechungmsft/jsonkt/shared/`.
- Build logic is managed in `buildSrc/`.
