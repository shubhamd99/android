import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    id("com.android.library")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

android {
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.findVersion("jvmTarget").get().requiredVersion)
        targetCompatibility = JavaVersion.toVersion(libs.findVersion("jvmTarget").get().requiredVersion)
    }
}

kotlin {
    jvmToolchain(libs.findVersion("jvmTarget").get().requiredVersion.toInt())
}
