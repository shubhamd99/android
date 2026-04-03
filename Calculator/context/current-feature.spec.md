# Feature Spec: Thermal Industrial Calculator — Android Jetpack Compose

## Reference Assets

- `design.png` — Full UI design showing Basic Mode (left) and Advanced/Scientific Mode (right) of the calculator. Dark background with orange accent, circular buttons, and a large orange display panel.
- `dls.png` — Thermal Industrial Design Language System (DLS) showing the full token set: color palette, typography, button variants, and component library.

---

## Overview

Build a fully functional, pixel-faithful calculator app for Android using Jetpack Compose. The app must implement two distinct modes — Basic and Scientific — toggleable via a chip/badge in the display header. All visual tokens must strictly follow the Thermal Industrial DLS defined in `dls.png`.

---

## Design Language System (from dls.png)

### Color Tokens

| Token     | Hex       | Usage                                                     |
| --------- | --------- | --------------------------------------------------------- |
| Primary   | `#FF4500` | Display panel background, equals button, active accents   |
| Secondary | `#212121` | Button keypad background, inactive button surfaces        |
| Tertiary  | `#008BFB` | Reserved (not used in calculator, but available in theme) |
| Neutral   | `#121212` | App/screen background, darkest surface                    |

Button surface shades come from the secondary palette swatches:

- Standard digit/function buttons: approximately `#2C2C2C` to `#333333`
- Operator buttons (-, +, x, /): slightly lighter dark gray, approximately `#3A3A3A`
- Equals button: `#FF4500` (Primary)
- Special row buttons (AC, backspace, %): medium dark gray

### Typography

| Role     | Typeface      | Usage                                   |
| -------- | ------------- | --------------------------------------- |
| Headline | Space Grotesk | Display result number, mode badge label |
| Body     | Inter         | Expression/equation preview line        |
| Label    | Space Grotesk | All button labels                       |

Font sizes:

- Result display: `48sp` to `64sp`, bold, white
- Expression preview: `16sp`, medium weight, muted white (`#FFFFFF99` or similar)
- Button labels: `18sp` to `22sp`, medium weight, white
- Mode badge: `11sp`, bold, uppercase letter-spaced, white

### Button Variants (from dls.png)

- Primary (filled orange): equals `=` button only
- Secondary (filled dark): all digit and standard function buttons
- Inverted / Outlined: mode toggle chip — pill shape with border, label text, and an arrow/swap icon

---

## Screen Architecture

The app has a single screen with two layout states controlled by `mode: CalculatorMode` (Basic or Advanced).

### Shared Structure

```
Screen (Neutral #121212 background)
  └── CalculatorScaffold
        ├── DisplayPanel         (Primary #FF4500, top ~40% of screen)
        │     ├── ModeToggleChip (top-left, outlined/inverted style)
        │     ├── ExpressionText (right-aligned, muted, above result)
        │     └── ResultText     (right-aligned, large bold, Space Grotesk)
        └── KeypadPanel          (Secondary #212121 background, bottom ~60%)
              └── ButtonGrid     (mode-dependent layout)
```

---

## Mode 1: Basic Calculator (design.png — left panel)

### Display Panel

- Mode badge top-left: `BASIC MODE` with a swap/toggle icon (outlined pill button)
- Expression row (right-aligned): shows current expression, e.g. `1,250.50 * 0.15`
- Result row (right-aligned): large bold result, e.g. `187.575`

### Keypad Layout — 5 rows x 4 columns

```
Row 1:  AC    [backspace]    %      -
Row 2:   7        8          9      x
Row 3:   4        5          6      -
Row 4:   1        2          3      +
Row 5:   .        0         EXP     =
```

Notes:

- `=` is the only orange (Primary) button
- All buttons are circular/pill shaped with equal sizing
- `[backspace]` shows a left-arrow-with-x icon (not text)
- Operator column buttons (-, x, +, /) use a slightly distinct shade from digit buttons
- `AC` uses the same secondary button style as digits
- No scrollable content; all buttons fixed in a uniform grid

---

## Mode 2: Scientific Calculator (design.png — right panel)

### Display Panel

- Mode badge top-left: `ADVANCED MODE` with swap icon (same outlined pill style)
- Expression row (right-aligned): e.g. `sin(45) * log(100) / π`
- Result row (right-aligned): e.g. `22.4589012` — same large bold Space Grotesk

### Top Control Bar (between display and keypad)

A single row of 4 utility chips/buttons above the main keypad:

```
INV    DEG    F-E    CLEAN
```

These are flat/outlined pill buttons in a lighter dark style (not the same circular shape as keypad buttons).

### Keypad Layout — 6 rows x 5 columns

```
Row 1:  sin    cos    tan     π      AC
Row 2:   ln    log     √      (       )
Row 3:   x^y    7      8      9       /
Row 4:    e     4      5      6       x
Row 5:   x!     1      2      3       -
Row 6:  Ans     0      .      =       +
```

Notes:

- `=` is the only orange (Primary) button
- Scientific function buttons (sin, cos, tan, ln, log, √, π, x^y, e, x!, Ans) use a distinctly darker black button surface compared to digit buttons, creating a visual hierarchy
- Digit buttons (0-9) use the standard secondary dark gray
- Operator buttons (/, x, -, +) use the slightly lighter gray variant
- `(` and `)` use the same dark function button style
- `AC` uses standard secondary style

---

## Interaction Behavior

### Input Logic

- Tapping digits appends to current input
- Tapping an operator after a result starts a new chained expression (using result as operand)
- `AC` clears all state (expression + result)
- Backspace removes the last character of the current input token
- `EXP` (Basic) appends scientific notation `E` to current number
- `=` evaluates the full expression and shows result; expression line shows what was evaluated

### Scientific Function Behavior

- `sin`, `cos`, `tan`: wrap current number or open a function call, e.g. `sin(`
- `INV`: toggles inverse functions (sin -> asin, cos -> acos, tan -> atan, ln -> e^x, log -> 10^x)
- `DEG` / `RAD`: toggles angle mode; button label updates to reflect current mode
- `F-E`: toggles between fixed and scientific (exponential) notation for the result
- `CLEAN`: same as `AC`, clears everything
- `π`: inserts the constant π value
- `e`: inserts Euler's number e
- `√`: inserts square root function `√(`
- `x^y`: inserts power operator `^`
- `x!`: appends factorial operator `!`
- `ln`: natural log function `ln(`
- `log`: base-10 log `log(`
- `Ans`: inserts the last computed result into the current expression
- `(` / `)`: inserts parentheses for grouping

### Expression Evaluation

- Use a safe expression parser (do not use `eval`)
- Support operator precedence, parentheses, and standard math functions
- Show errors gracefully: if expression is invalid, display `Error` in result with the expression preserved in the expression line
- Numbers with more than 10 significant digits should use scientific notation automatically

---

## Animation and Transitions

- Mode toggle (Basic <-> Advanced): animate the keypad swap with a horizontal slide or crossfade, 250ms, `EaseInOutCubic`
- Button press: scale down to `0.92f` on press, spring back on release (`spring(stiffness = Spring.StiffnessMedium)`)
- Result update: if result changes due to `=`, animate a subtle fade-in on the result text
- INV active state: INV button should have a highlighted/active visual state (e.g. lighter background or primary tint) when inverse mode is on

---

## Module Architecture

This feature follows the scalable multi-module structure. The calculator lives entirely inside `feature/calculator`. Shared tokens go into `core/designsystem`. The expression parser utility goes into `core/common`. No business logic touches `app/`.

### Module Responsibility Map

| Module                | What goes here                                                   |
| --------------------- | ---------------------------------------------------------------- |
| `app/`                | `MainActivity`, `App`, `AppNavHost`, `AppModule` — wiring only   |
| `core/designsystem/`  | Thermal Industrial color, typography, shape, spacing tokens      |
| `core/common/`        | `ExpressionParser`, `ResultWrapper`, extension functions         |
| `core/ui/components/` | `CalcButton` — shared if reused elsewhere; else stays in feature |
| `feature/calculator/` | Everything specific to the calculator feature                    |
| `navigation/`         | `Routes.kt` route constant for calculator screen                 |

---

## Folder Structure

```
root/
│
├── app/
│   ├── MainActivity.kt                         # setContent -> AppNavHost
│   ├── App.kt                                  # @HiltAndroidApp
│   ├── navigation/
│   │   └── AppNavHost.kt                       # navController, composable(Routes.Calculator)
│   └── di/
│       └── AppModule.kt
│
├── core/
│
│   ├── designsystem/
│   │   ├── Color.kt                            # Thermal Industrial tokens (Primary #FF4500, etc.)
│   │   ├── Theme.kt                            # ThermalIndustrialTheme {}
│   │   ├── Typography.kt                       # Space Grotesk (Headline/Label), Inter (Body)
│   │   ├── Shape.kt                            # CircleShape for buttons, RoundedCorner for chips
│   │   └── Spacing.kt                          # 4dp grid spacing tokens
│   │
│   ├── common/
│   │   ├── extensions/
│   │   │   └── StringExtensions.kt             # isOperator(), isFunction(), formatResult()
│   │   ├── constants/
│   │   │   └── MathConstants.kt                # PI, E string representations
│   │   └── ResultWrapper.kt                    # sealed class Success / Error / Loading
│   │
│   └── ui/
│       └── components/
│           └── CalcButton.kt                   # Reusable across any future calculator-adjacent feature
│                                               # Props: label, type: ButtonType, onClick, modifier
│
├── feature/
│   └── calculator/
│       │
│       ├── domain/
│       │   ├── model/
│       │   │   ├── CalculatorMode.kt           # enum: BASIC, ADVANCED
│       │   │   ├── AngleMode.kt                # enum: DEG, RAD
│       │   │   └── ButtonType.kt               # enum: DIGIT, OPERATOR, FUNCTION, EQUALS, UTILITY
│       │   │
│       │   └── usecase/
│       │       ├── EvaluateExpressionUseCase.kt    # wraps ExpressionParser, returns ResultWrapper
│       │       └── FormatResultUseCase.kt          # handles F-E toggle, sig-fig limits, auto sci-notation
│       │
│       ├── ui/
│       │   │
│       │   ├── components/                     # Reusable within this feature only
│       │   │   ├── DisplayPanel.kt             # Orange top panel: ModeChip + ExpressionText + ResultText
│       │   │   ├── ModeChip.kt                 # Outlined pill: "BASIC MODE / ADVANCED MODE" + swap icon
│       │   │   └── ScientificControlBar.kt     # INV / DEG / F-E / CLEAN chip row
│       │   │
│       │   └── screens/
│       │       └── calculator/
│       │           ├── CalculatorRoute.kt      # Stateful: hiltViewModel(), passes state + callbacks down
│       │           ├── CalculatorScreen.kt     # Stateless: receives state + lambdas, no VM dependency
│       │           ├── CalculatorContent.kt    # Layout: DisplayPanel + conditional keypad
│       │           │
│       │           ├── components/             # Screen-specific only
│       │           │   ├── BasicKeypad.kt      # 5x4 grid (design.png left)
│       │           │   └── ScientificKeypad.kt # 6x5 grid (design.png right)
│       │           │
│       │           ├── CalculatorState.kt      # data class CalculatorState(...)
│       │           ├── CalculatorEvent.kt      # sealed class CalculatorEvent(...)
│       │           └── CalculatorViewModel.kt  # @HiltViewModel, holds state, processes events
│       │
│       └── di/
│           └── CalculatorModule.kt             # @Module @InstallIn(ViewModelComponent) bindings
│
├── navigation/
│   ├── Routes.kt                               # object Routes { const val Calculator = "calculator" }
│   └── Navigator.kt
│
├── build-logic/
│   └── convention/
│       ├── android-application.gradle.kts
│       ├── android-library.gradle.kts
│       ├── feature-module.gradle.kts           # apply to feature/calculator/build.gradle.kts
│       └── hilt-module.gradle.kts
│
└── settings.gradle.kts                         # include(":feature:calculator"), (":core:designsystem"), etc.
```

---

## Component Breakdown (Composables)

```
AppNavHost
  └── CalculatorRoute              # hiltViewModel() lives here only
        └── CalculatorScreen(state, onEvent)
              └── CalculatorContent(state, onEvent)
                    ├── DisplayPanel(state, onEvent)
                    │     ├── ModeChip(mode, onClick = { onEvent(ToggleMode) })
                    │     ├── ExpressionText(expression)
                    │     └── ResultText(result)
                    │
                    ├── ScientificControlBar(...)     // only when mode == ADVANCED
                    │     └── CalcButton(type = UTILITY)  x4
                    │
                    ├── BasicKeypad(onEvent)          // only when mode == BASIC
                    │     └── CalcButton(type = DIGIT | OPERATOR | EQUALS)
                    │
                    └── ScientificKeypad(onEvent)     // only when mode == ADVANCED
                          └── CalcButton(type = DIGIT | OPERATOR | FUNCTION | EQUALS)
```

### CalcButton — defined in `core/ui/components/CalcButton.kt`

```kotlin
enum class ButtonType {
    DIGIT,        // surface: #2C2C2C, label: white
    OPERATOR,     // surface: #3A3A3A, label: white
    FUNCTION,     // surface: #1A1A1A, label: white (darkest — scientific keys)
    EQUALS,       // surface: #FF4500 (Primary), label: white
    UTILITY       // outlined pill, no fill, border: #3A3A3A (INV, DEG, F-E, CLEAN)
}
```

---

## ViewModel State & Events

Defined in `feature/calculator/ui/screens/calculator/`

```kotlin
// CalculatorState.kt
data class CalculatorState(
    val mode: CalculatorMode = CalculatorMode.BASIC,
    val expression: String = "",
    val result: String = "0",
    val isInverse: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEG,
    val isScientificNotation: Boolean = false,
    val lastResult: String = "0",
    val hasError: Boolean = false
)

// CalculatorEvent.kt
sealed class CalculatorEvent {
    data class DigitPressed(val digit: String) : CalculatorEvent()
    data class OperatorPressed(val op: String) : CalculatorEvent()
    data class FunctionPressed(val fn: String) : CalculatorEvent()
    object EqualsPressed : CalculatorEvent()
    object ClearPressed : CalculatorEvent()
    object BackspacePressed : CalculatorEvent()
    object ToggleMode : CalculatorEvent()
    object ToggleInverse : CalculatorEvent()
    object ToggleAngleMode : CalculatorEvent()
    object ToggleFormat : CalculatorEvent()
}

// CalculatorViewModel.kt
@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val evaluateExpression: EvaluateExpressionUseCase,
    private val formatResult: FormatResultUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onEvent(event: CalculatorEvent) { /* ... */ }
}
```

CalculatorRoute collects state and wires events:

```kotlin
// CalculatorRoute.kt
@Composable
fun CalculatorRoute(viewModel: CalculatorViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CalculatorScreen(state = state, onEvent = viewModel::onEvent)
}
```

---

## Use Cases

Defined in `feature/calculator/domain/usecase/`

### EvaluateExpressionUseCase.kt

- Accepts the raw expression string
- Delegates to `ExpressionParser` from `core/common`
- Returns `ResultWrapper.Success<String>` or `ResultWrapper.Error`
- Handles angle mode conversion (deg to rad) before passing trig values

### FormatResultUseCase.kt

- Accepts raw Double result and current `isScientificNotation` flag
- Applies 10-significant-digit cap
- Switches to `%.6E` notation if result exceeds display threshold or flag is set
- Returns formatted String ready for display

---

## Convention Plugin Usage

`feature/calculator/build.gradle.kts` applies:

```kotlin
plugins {
    id("convention.feature-module")   // includes compose, hilt, coroutines
    id("convention.hilt-module")
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":navigation"))
    implementation("net.objecthunter:exp4j:0.4.8")
}
```

`core/designsystem/build.gradle.kts` applies:

```kotlin
plugins {
    id("convention.android-library")
}
dependencies {
    api("androidx.compose.ui:ui")
    api("androidx.compose.material3:material3")
    api("androidx.compose.ui:ui-text-google-fonts")
}
```

---

## Navigation Registration

`navigation/Routes.kt`:

```kotlin
object Routes {
    const val Calculator = "calculator"
}
```

`app/navigation/AppNavHost.kt`:

```kotlin
composable(Routes.Calculator) {
    CalculatorRoute()
}
```

---

## Accessibility

- All buttons must have `contentDescription` set; backspace icon uses `"Backspace"`
- `ResultText` applies `Modifier.semantics { liveRegion = LiveRegionMode.Polite }` so screen readers announce result changes
- Minimum touch target: `48dp x 48dp` enforced via `Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)`
- All text on button surfaces must meet WCAG AA contrast ratio (4.5:1 minimum)

---

## Out of Scope

- History / memory storage (M+, M-, MR)
- Landscape layout adaptation
- Widget or home screen shortcut
- Haptic feedback (can be added as enhancement)
- Unit conversion or currency mode

## Rules

- Use latest Jetpack and Jetpack compose libraries and components while building the feature.
- Use web search to find the latest libraries and components and best practices to build the feature.
