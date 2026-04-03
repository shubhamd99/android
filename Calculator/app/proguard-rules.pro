# ProGuard/R8 rules for Calculator App

# ---------------------------------------------------------
# Kotlin & Coroutines
# ---------------------------------------------------------

# Keep generic signatures for reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod,InnerClasses
-keepattributes SourceFile,LineNumberTable

# ---------------------------------------------------------
# Kotlinx Serialization
# ---------------------------------------------------------

# The library includes consumer rules, but we can be explicit
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepnames @kotlinx.serialization.Serializable class *

# Keep all navigation routes (especially for Navigation 3)
-keep class com.shubhamtechie.calculator.navigation.** { *; }

# ---------------------------------------------------------
# Hilt / Dagger
# ---------------------------------------------------------

-keep class dagger.hilt.** { *; }
-keep @dagger.Module class *
-keep class com.shubhamtechie.calculator.**_HiltComponents* { *; }
-keep class com.shubhamtechie.calculator.**_GeneratedInjector { *; }
-keep class com.shubhamtechie.calculator.di.** { *; }

# ---------------------------------------------------------
# Compose
# ---------------------------------------------------------

-keep class androidx.compose.ui.platform.** { *; }

# ---------------------------------------------------------
# Project Specific (Models & Routes)
# ---------------------------------------------------------

# Keep all data models and state classes to prevent stripping of fields
-keep class com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.CalculatorState { *; }
-keep class com.shubhamtechie.calculator.feature.calculator.domain.model.** { *; }
-keep class com.shubhamtechie.calculator.core.common.** { *; }

# Keep all navigation routes
-keep class com.shubhamtechie.calculator.navigation.Screen { *; }

# ---------------------------------------------------------
# exp4j (Expression Parser)
# ---------------------------------------------------------
-keep class net.objecthunter.exp4j.** { *; }
