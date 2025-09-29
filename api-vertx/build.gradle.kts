plugins {
    kotlin("jvm")
    application
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.github.jsonkt.apivertx.ApplicationKt")
}

// Configure shadowJar to set the main class in the manifest
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    manifest {
        attributes["Main-Class"] = "com.github.jsonkt.apivertx.ApplicationKt"
    }
}

dependencies {
    implementation(project(":json"))
    implementation(libs.vertx.core)
    implementation(libs.vertx.web)
    implementation(libs.vertx.lang.kotlin)
    implementation(libs.vertx.lang.kotlin.coroutines)
    implementation(libs.kotlinx.coroutines.core)
}
