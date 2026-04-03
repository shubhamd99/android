# Project Context: Calculator App (Android)

This project is a modern Android Calculator application built with Jetpack Compose, Hilt, and Navigation 3, following a modularized architecture.

## Technical Stack (April 2026)
- **Kotlin:** 2.3.20 (K2 compiler is default)
- **AGP:** 9.1.0 (Note: AGP 9.0+ handles Kotlin support internally; the `org.jetbrains.kotlin.android` plugin is **NOT** required)
- **Compose:** Multiplatform-ready, requires `org.jetbrains.kotlin.plugin.compose` for Kotlin 2.0+
- **Navigation:** Navigation 3 (Stable 1.0.1)
- **DI:** Hilt 2.59.2

## Navigation 3 (Stable 1.0.1) Best Practices

### Correct Imports
Always use these specific package mappings to avoid "Unresolved reference" errors:
- `androidx.navigation3.ui.NavDisplay` (UI observer)
- `androidx.navigation3.runtime.rememberNavBackStack` (State management)
- `androidx.navigation3.runtime.entryProvider` (DSL builder)
- `androidx.navigation3.runtime.entry` (DSL extension for EntryProviderScope)
- `androidx.navigation3.NavEntry` (Data class for entries)

### Standard Implementation Pattern
```kotlin
val backStack = rememberNavBackStack(Screen.Home)

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider {
        entry<Screen.Home> {
            HomeScreen()
        }
    }
)
```

## Build Logic & Convention Plugins
- **Precompiled Scripts:** Use `.gradle.kts` files in `build-logic/src/main/kotlin/` for convention plugins.
- **Kotlin Integration:** Since AGP 9.0, do not apply the Kotlin Android plugin manually in library/application modules. AGP detects Kotlin automatically.
- **Compose Compiler:** The `org.jetbrains.kotlin.plugin.compose` must be applied to any module using Compose.

## Resource Handling
- **Adaptive Icons:** Adaptive icon XMLs (e.g., in `mipmap-anydpi-v26`) require a minimum SDK version of 26. Ensure the directory is correctly suffixed if `minSdk` is lower.

## Project Structure
The project follows a modularized "Clean Architecture" inspired structure:

- **`app/`**: The main Android application module. Orchestrates navigation and dependency injection (Hilt).
- **`build-logic/`**: Contains precompiled script plugins (`.gradle.kts`) to share build configuration across modules.
- **`core/`**
    - **`common/`**: Pure Kotlin/Java logic (e.g., `ExpressionParser`, `ResultWrapper`). No Android dependencies.
    - **`designsystem/`**: Atomic design components, `ThermalIndustrialTheme`, Colors, and Typography.
    - **`ui/`**: Shared Compose UI components used across multiple features.
- **`feature/`**
    - **`calculator/`**: Implementation of the calculator screen, its ViewModel, and domain-specific UseCases.
- **`navigation/`**: Centralized navigation keys (`Screen` interface) and `@Serializable` route definitions.
- **`gradle/`**: Project-wide dependency management via `libs.versions.toml`.
