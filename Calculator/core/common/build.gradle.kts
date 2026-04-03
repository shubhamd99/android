plugins {
    id("convention.android-library")
}

android {
    namespace = "com.shubhamtechie.calculator.core.common"
}

dependencies {
    implementation(libs.exp4j)
    implementation(libs.javax.inject)
}
