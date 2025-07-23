plugins {
    kotlin("jvm")
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.gchat.backend"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.cio.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.logback.classic)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2.database)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.jbcrypt) // New dependency
    testImplementation(libs.ktor.server.tests.jvm)
    testImplementation(libs.kotlin.test.junit)
}
