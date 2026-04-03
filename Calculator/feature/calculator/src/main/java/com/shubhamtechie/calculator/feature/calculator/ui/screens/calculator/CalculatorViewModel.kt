package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator

import androidx.lifecycle.ViewModel
import com.shubhamtechie.calculator.core.common.ResultWrapper
import com.shubhamtechie.calculator.feature.calculator.domain.model.AngleMode
import com.shubhamtechie.calculator.feature.calculator.domain.model.CalculatorMode
import com.shubhamtechie.calculator.feature.calculator.domain.usecase.EvaluateExpressionUseCase
import com.shubhamtechie.calculator.feature.calculator.domain.usecase.FormatResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val evaluateExpression: EvaluateExpressionUseCase,
    private val formatResult: FormatResultUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()

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
            // After = pressed, start a fresh expression
            val newExpr = if (it.justEvaluated || it.expression.isEmpty()) digit
                          else it.expression + digit
            it.copy(expression = newExpr, hasError = false, justEvaluated = false)
        }
    }

    private fun handleOperator(op: String) {
        _state.update {
            // After =, chain from the result; otherwise use current expression or lastResult
            val base = when {
                it.justEvaluated -> it.result
                it.expression.isEmpty() -> it.lastResult
                else -> it.expression
            }
            it.copy(
                expression = if (base == "Error") "" else "$base $op ",
                justEvaluated = false
            )
        }
    }

    private fun handleFunction(fn: String) {
        _state.update {
            // After =, start fresh with the function call
            val base = if (it.justEvaluated) "" else it.expression
            it.copy(expression = "$base$fn(", justEvaluated = false)
        }
    }

    private fun handleBackspace() {
        _state.update {
            when {
                it.justEvaluated -> {
                    // Backspace after = clears the displayed expression; result stays
                    it.copy(expression = "", justEvaluated = false)
                }
                it.expression.isNotEmpty() -> {
                    // Drop " op " (3 chars) when expression ends with an operator+space
                    val newExpr = if (it.expression.length >= 3 && it.expression.endsWith(" ")) {
                        it.expression.dropLast(3)
                    } else {
                        it.expression.dropLast(1)
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

        val result = evaluateExpression(exprToEval, currentState.angleMode, currentState.lastResult)
        when (result) {
            is ResultWrapper.Success -> {
                val formatted = formatResult(result.data, currentState.isScientificNotation)
                _state.update {
                    it.copy(
                        result = formatted,
                        lastResult = formatted,
                        rawResult = result.data,
                        // Keep the evaluated expression in the expression line (spec requirement)
                        expression = exprToEval,
                        justEvaluated = true,
                        hasError = false
                    )
                }
            }
            is ResultWrapper.Error -> {
                _state.update { it.copy(result = "Error", hasError = true, justEvaluated = false) }
            }
            else -> {}
        }
    }

    private fun reformatResult() {
        val currentState = _state.value
        val raw = currentState.rawResult ?: return
        val formatted = formatResult(raw, currentState.isScientificNotation)
        _state.update { it.copy(result = formatted, lastResult = formatted) }
    }
}
