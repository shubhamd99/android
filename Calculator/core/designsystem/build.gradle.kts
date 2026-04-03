plugins {
    id("convention.android-library")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.shubhamtechie.calculator.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.text.google.fonts)
}
