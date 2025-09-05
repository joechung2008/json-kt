plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("joechungmsft.apivertx.ApplicationKt")
}

dependencies {
    implementation(project(":json"))
    implementation(libs.vertx.core)
    implementation(libs.vertx.web)
    implementation(libs.vertx.lang.kotlin)
    implementation(libs.vertx.lang.kotlin.coroutines)
    implementation(libs.kotlinx.coroutines.core)
}
