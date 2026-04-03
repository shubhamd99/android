package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamtechie.calculator.core.common.ResultWrapper
import com.shubhamtechie.calculator.feature.calculator.domain.model.AngleMode
import com.shubhamtechie.calculator.feature.calculator.domain.model.CalculatorMode
import com.shubhamtechie.calculator.feature.calculator.domain.usecase.EvaluateExpressionUseCase
import com.shubhamtechie.calculator.feature.calculator.domain.usecase.FormatResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val evaluateExpression: EvaluateExpressionUseCase,
    private val formatResult: FormatResultUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        CalculatorState(
            expression = savedStateHandle[KEY_EXPRESSION] ?: "",
            result = savedStateHandle[KEY_RESULT] ?: "0",
            lastResult = savedStateHandle[KEY_LAST_RESULT] ?: "0",
            rawResult = savedStateHandle.get<Double>(KEY_RAW_RESULT),
            isScientificNotation = savedStateHandle[KEY_IS_SCIENTIFIC] ?: false,
            angleMode = AngleMode.valueOf(savedStateHandle[KEY_ANGLE_MODE] ?: AngleMode.DEG.name),
            mode = CalculatorMode.valueOf(savedStateHandle[KEY_MODE] ?: CalculatorMode.BASIC.name),
            isInverse = savedStateHandle[KEY_IS_INVERSE] ?: false,
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.collect { s ->
                savedStateHandle[KEY_EXPRESSION] = s.expression
                savedStateHandle[KEY_RESULT] = s.result
                savedStateHandle[KEY_LAST_RESULT] = s.lastResult
                savedStateHandle[KEY_IS_SCIENTIFIC] = s.isScientificNotation
                savedStateHandle[KEY_ANGLE_MODE] = s.angleMode.name
                savedStateHandle[KEY_MODE] = s.mode.name
                savedStateHandle[KEY_IS_INVERSE] = s.isInverse
                if (s.rawResult != null) {
                    savedStateHandle[KEY_RAW_RESULT] = s.rawResult
                } else {
                    savedStateHandle.remove<Double>(KEY_RAW_RESULT)
                }
            }
        }
    }

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.DigitPressed -> handleDigit(event.digit)
            is CalculatorEvent.OperatorPressed -> handleOperator(event.op)
            is CalculatorEvent.FunctionPressed -> handleFunction(event.fn)
            CalculatorEvent.EqualsPressed -> evaluate()
            CalculatorEvent.ClearPressed -> _state.update { CalculatorState(mode = it.mode) }
            CalculatorEvent.BackspacePressed -> handleBackspace()
            CalculatorEvent.ToggleMode -> _state.update {
                it.copy(mode = if (it.mode == CalculatorMode.BASIC) CalculatorMode.ADVANCED else CalculatorMode.BASIC)
            }
            CalculatorEvent.ToggleInverse -> _state.update { it.copy(isInverse = !it.isInverse) }
            CalculatorEvent.ToggleAngleMode -> _state.update {
                it.copy(angleMode = if (it.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG)
            }
            CalculatorEvent.ToggleFormat -> {
                _state.update { it.copy(isScientificNotation = !it.isScientificNotation) }
                reformatResult()
            }
        }
    }

    private fun handleDigit(digit: String) {
        _state.update {
            val lastToken = it.expression.split(Regex("[\\s+\\-x÷^()]+")).lastOrNull() ?: ""
            if (digit == ".") {
                // Prevent multiple decimal points in a single numeric token
                if (lastToken.contains(".") || lastToken == "Ans" || lastToken == "π" || lastToken == "e") {
                    return@update it
                }
            }

            if (digit == "E") {
                // EXP requires a digit to attach to — guard against bare E in the expression
                val base = if (it.justEvaluated) it.result else it.expression
                val lastChar = base.trimEnd().lastOrNull()
                if (lastChar == null || (!lastChar.isDigit() && lastChar != '.')) return@update it
                
                // After =, append E to the result rather than starting fresh
                if (it.justEvaluated) {
                    return@update it.copy(
                        expression = base.trimEnd() + "E",
                        hasError = false,
                        justEvaluated = false
                    )
                }
            }

            val newExpr = if (it.justEvaluated || it.expression.isEmpty()) {
                if (digit == ".") "0." else digit
            } else {
                it.expression + digit
            }
            it.copy(expression = newExpr, hasError = false, justEvaluated = false)
        }
    }

    private fun handleOperator(op: String) {
        _state.update {
            var currentExpr = if (it.justEvaluated) it.result else it.expression
            if (currentExpr == "Error") currentExpr = ""

            val operators = listOf("+", "-", "x", "÷", "^")
            val trimmed = currentExpr.trimEnd()
            
            val newExpr = when {
                trimmed.isEmpty() -> {
                    if (it.lastResult == "Error" || it.lastResult == "0") "0 $op " 
                    else "${it.lastResult} $op "
                }
                operators.any { o -> trimmed.endsWith(o) } -> {
                    // Replace the last operator
                    trimmed.dropLast(1).trimEnd() + " $op "
                }
                else -> {
                    "$trimmed $op "
                }
            }

            it.copy(
                expression = newExpr,
                justEvaluated = false
            )
        }
    }

    private fun handleFunction(fn: String) {
        _state.update {
            val base = if (it.justEvaluated) "" else it.expression
            it.copy(expression = "$base$fn(", justEvaluated = false)
        }
    }

    private fun handleBackspace() {
        _state.update {
            when {
                it.justEvaluated -> it.copy(expression = "", justEvaluated = false)
                it.expression.isNotEmpty() -> {
                    val expr = it.expression
                    val newExpr = when {
                        expr.endsWith(" ") -> expr.dropLast(3) // For operators like " + "
                        expr.endsWith("Ans") -> expr.dropLast(3)
                        expr.endsWith("sin(") || expr.endsWith("cos(") || expr.endsWith("tan(") -> expr.dropLast(4)
                        expr.endsWith("asin(") || expr.endsWith("acos(") || expr.endsWith("atan(") -> expr.dropLast(5)
                        expr.endsWith("log(") || expr.endsWith("exp(") -> expr.dropLast(4)
                        expr.endsWith("ln(") -> expr.dropLast(3)
                        expr.endsWith("sqrt(") -> expr.dropLast(5)
                        else -> expr.dropLast(1)
                    }
                    it.copy(expression = newExpr)
                }
                else -> it
            }
        }
    }

    private fun evaluate() {
        val currentState = _state.value
        val exprToEval = currentState.expression.trim()
        if (exprToEval.isEmpty()) return

        when (val result = evaluateExpression(exprToEval, currentState.angleMode, currentState.lastResult)) {
            is ResultWrapper.Success -> {
                val formatted = formatResult(result.data, currentState.isScientificNotation)
                _state.update {
                    it.copy(
                        result = formatted,
                        lastResult = formatted,
                        rawResult = result.data,
                        expression = exprToEval,
                        justEvaluated = true,
                        hasError = false
                    )
                }
            }
            is ResultWrapper.Error -> {
                _state.update { it.copy(result = "Error", hasError = true, justEvaluated = false) }
            }
        }
    }

    private fun reformatResult() {
        val currentState = _state.value
        val raw = currentState.rawResult ?: return
        val formatted = formatResult(raw, currentState.isScientificNotation)
        _state.update { it.copy(result = formatted, lastResult = formatted) }
    }

    companion object {
        private const val KEY_EXPRESSION = "expression"
        private const val KEY_RESULT = "result"
        private const val KEY_LAST_RESULT = "lastResult"
        private const val KEY_RAW_RESULT = "rawResult"
        private const val KEY_IS_SCIENTIFIC = "isScientificNotation"
        private const val KEY_ANGLE_MODE = "angleMode"
        private const val KEY_MODE = "mode"
        private const val KEY_IS_INVERSE = "isInverse"
    }
}
