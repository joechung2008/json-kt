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

## Notes

- The CLI entry point is in `app/src/main/kotlin/joechungmsft/jsonkt/cli/App.kt`.
- The parser library code is in `json/src/main/kotlin/joechungmsft/jsonkt/shared/`.
- Build logic is managed in `buildSrc/`.
