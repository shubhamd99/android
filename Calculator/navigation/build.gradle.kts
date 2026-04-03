plugins {
    id("convention.android-library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.shubhamtechie.calculator.navigation"
}

dependencies {
    implementation(libs.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)
}
