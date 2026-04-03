package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator

sealed class CalculatorEvent {
    data class DigitPressed(val digit: String) : CalculatorEvent()
    data class OperatorPressed(val op: String) : CalculatorEvent()
    data class FunctionPressed(val fn: String) : CalculatorEvent()
    data object EqualsPressed : CalculatorEvent()
    data object ClearPressed : CalculatorEvent()
    data object BackspacePressed : CalculatorEvent()
    data object ToggleMode : CalculatorEvent()
    data object ToggleInverse : CalculatorEvent()
    data object ToggleAngleMode : CalculatorEvent()
    data object ToggleFormat : CalculatorEvent()
}
