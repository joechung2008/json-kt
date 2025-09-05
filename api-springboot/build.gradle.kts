plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.5"
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("joechungmsft.apispringboot.ApplicationKt")
}

dependencies {
    implementation(project(":json"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.kotlinx.serialization.json)
}
