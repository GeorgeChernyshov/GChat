import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet // Required for named<KotlinSourceSet>

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvmToolchain(17)
    androidTarget()
    jvm()
    
    val iosTargets = listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    iosTargets.forEach {
        it.binaries.framework {
            baseName = "shared"
        }
        it.compilations.getByName("main").defaultSourceSet.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }

    sourceSets {
        sourceSets.named<KotlinSourceSet>("commonMain") {
            dependencies {
                //compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended) // Added for Icons.Filled.Visibility

                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kermit)

                //koin
                implementation(libs.koin.compose.viewmodel)

                //ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.core)

                // Voyager
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)
            }
        }

        sourceSets.named<KotlinSourceSet>("androidMain") {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}

android {
    namespace = "com.gchat.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() 
    }
}