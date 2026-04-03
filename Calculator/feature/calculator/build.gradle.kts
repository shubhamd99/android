plugins {
    id("convention.feature-module")
    id("convention.hilt-module")
}

android {
    namespace = "com.shubhamtechie.calculator.feature.calculator"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":navigation"))
    
    implementation(libs.exp4j)
}
