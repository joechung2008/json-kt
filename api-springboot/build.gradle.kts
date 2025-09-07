plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.github.jsonkt.apispringboot.ApplicationKt")
}

dependencies {
    implementation(project(":json"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.kotlinx.serialization.json)
}
