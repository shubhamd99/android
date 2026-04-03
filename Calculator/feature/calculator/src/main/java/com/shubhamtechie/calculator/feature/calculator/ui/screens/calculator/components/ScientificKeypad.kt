package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shubhamtechie.calculator.core.ui.components.ButtonType
import com.shubhamtechie.calculator.core.ui.components.CalcButton
import com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.CalculatorEvent

@Composable
fun ScientificKeypad(
    isInverse: Boolean,
    onEvent: (CalculatorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val sinLabel = if (isInverse) "asin" else "sin"
    val cosLabel = if (isInverse) "acos" else "cos"
    val tanLabel = if (isInverse) "atan" else "tan"
    val lnLabel = if (isInverse) "eˣ" else "ln"
    val logLabel = if (isInverse) "10ˣ" else "log"

    val buttons = listOf(
        listOf(sinLabel, cosLabel, tanLabel, "π", "AC"),
        listOf(lnLabel, logLabel, "√", "x^y", "÷"),
        listOf("(", "7", "8", "9", "x"),
        listOf(")", "4", "5", "6", "-"),
        listOf("x!", "1", "2", "3", "+"),
        listOf("e", ".", "0", "Ans", "=")
    )

    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { label ->
                    val type = when {
                        label == "=" -> ButtonType.EQUALS
                        label in listOf("÷", "x", "-", "+") -> ButtonType.OPERATOR
                        label == "AC" -> ButtonType.UTILITY
                        label in listOf("7", "8", "9", "4", "5", "6", "1", "2", "3", "0", ".") -> ButtonType.DIGIT
                        else -> ButtonType.FUNCTION
                    }

                    CalcButton(
                        label = label,
                        type = type,
                        onClick = {
                            when (label) {
                                "AC" -> onEvent(CalculatorEvent.ClearPressed)
                                "=" -> onEvent(CalculatorEvent.EqualsPressed)
                                in listOf("÷", "x", "-", "+") -> onEvent(CalculatorEvent.OperatorPressed(label))
                                sinLabel, cosLabel, tanLabel -> onEvent(CalculatorEvent.FunctionPressed(label))
                                "√" -> onEvent(CalculatorEvent.FunctionPressed("√"))
                                "ln" -> onEvent(CalculatorEvent.FunctionPressed("ln"))
                                "log" -> onEvent(CalculatorEvent.FunctionPressed("log"))
                                // Inverse of ln: e^x — exp4j's built-in exp() function
                                "eˣ" -> onEvent(CalculatorEvent.FunctionPressed("exp"))
                                // Inverse of log₁₀: 10^x — append "10^" for user to type exponent
                                "10ˣ" -> onEvent(CalculatorEvent.DigitPressed("10^"))
                                "π" -> onEvent(CalculatorEvent.DigitPressed("π"))
                                "e" -> onEvent(CalculatorEvent.DigitPressed("e"))
                                "Ans" -> onEvent(CalculatorEvent.DigitPressed("Ans"))
                                "x^y" -> onEvent(CalculatorEvent.OperatorPressed("^"))
                                // x! is postfix: append "!" directly (no spaces) so exp4j parses "5!" correctly
                                "x!" -> onEvent(CalculatorEvent.DigitPressed("!"))
                                else -> onEvent(CalculatorEvent.DigitPressed(label))
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}
