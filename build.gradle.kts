plugins {
    id("com.github.ben-manes.versions") version "0.52.0" // Check for latest version
    id("org.jetbrains.kotlinx.kover") version "0.7.3"

    // https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")
}
