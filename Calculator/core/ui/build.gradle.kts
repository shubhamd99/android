plugins {
    id("convention.android-library")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.shubhamtechie.calculator.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
