import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    id("com.android.application")
    // Use the plugin id without the version, and it should be resolved from the root project
    id("org.jetbrains.kotlin.plugin.compose")
    id("convention.hilt-module")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

android {
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
        targetSdk = libs.findVersion("targetSdk").get().requiredVersion.toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.findVersion("jvmTarget").get().requiredVersion)
        targetCompatibility = JavaVersion.toVersion(libs.findVersion("jvmTarget").get().requiredVersion)
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(libs.findVersion("jvmTarget").get().requiredVersion.toInt())
}

dependencies {
    "implementation"(libs.findLibrary("androidx-core-ktx").get())
    "implementation"(libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
    "implementation"(libs.findLibrary("androidx-activity-compose").get())
    "implementation"(platform(libs.findLibrary("androidx-compose-bom").get()))
    "implementation"(libs.findLibrary("androidx-compose-ui").get())
    "implementation"(libs.findLibrary("androidx-compose-ui-graphics").get())
    "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
    "implementation"(libs.findLibrary("androidx-compose-material3").get())
}
