import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    application // Explicitly add the application plugin
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.shadow) // Add the shadow plugin
}

group = "com.gchat.backend"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
    // Alternatively, for Netty:
    // mainClass.set("io.ktor.server.netty.EngineMain")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.cio.jvm)
    // implementation(libs.ktor.server.netty.jvm) // If using Netty
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.kermit)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2.database) // For local testing/init
    implementation(libs.postgresql) // PostgreSQL driver
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.jbcrypt)
    implementation(project(":shared"))
    testImplementation(libs.ktor.server.tests.jvm)
    testImplementation(libs.kotlin.test.junit)
}

// ShadowJar configuration
tasks.withType<ShadowJar> {
    archiveBaseName.set("gchat-backend")      // Ensures the base name is "gchat-backend"
    archiveClassifier.set("")             // No extra classifier (like -all or -shadow)
    archiveVersion.set("0.0.1")               // Version
    mergeServiceFiles() // Important for Ktor to find modules correctly
    manifest {
        attributes(mapOf("Main-Class" to "com.gchat.backend.ApplicationKt")) // Correct Main Class
    }
}
