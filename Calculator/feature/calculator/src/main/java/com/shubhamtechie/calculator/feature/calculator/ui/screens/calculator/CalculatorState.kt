package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator

import com.shubhamtechie.calculator.feature.calculator.domain.model.AngleMode
import com.shubhamtechie.calculator.feature.calculator.domain.model.CalculatorMode

data class CalculatorState(
    val mode: CalculatorMode = CalculatorMode.BASIC,
    val expression: String = "",
    val result: String = "0",
    val isInverse: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEG,
    val isScientificNotation: Boolean = false,
    val lastResult: String = "0",
    val hasError: Boolean = false,
    // Tracks whether = was just pressed; next digit/function starts fresh
    val justEvaluated: Boolean = false,
    // Raw double result for F-E reformatting without re-evaluation
    val rawResult: Double? = null
)
