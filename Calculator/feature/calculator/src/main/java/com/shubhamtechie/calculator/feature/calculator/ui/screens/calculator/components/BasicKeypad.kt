package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shubhamtechie.calculator.core.ui.components.ButtonType
import com.shubhamtechie.calculator.core.ui.components.CalcButton
import com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.CalculatorEvent

@Composable
fun BasicKeypad(
    onEvent: (CalculatorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = listOf(
        listOf("AC", "DEL", "%", "÷"),
        listOf("7", "8", "9", "x"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf(".", "0", "EXP", "=")
    )

    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { label ->
                    val type = when {
                        label == "=" -> ButtonType.EQUALS
                        label in listOf("÷", "x", "-", "+") -> ButtonType.OPERATOR
                        label in listOf("AC", "DEL", "%", "EXP") -> ButtonType.UTILITY
                        else -> ButtonType.DIGIT
                    }

                    CalcButton(
                        label = label,
                        type = type,
                        onClick = {
                            when (label) {
                                "AC" -> onEvent(CalculatorEvent.ClearPressed)
                                "DEL" -> onEvent(CalculatorEvent.BackspacePressed)
                                // % appended as digit so user can type "50%" and evaluate
                                "%" -> onEvent(CalculatorEvent.DigitPressed("%"))
                                // EXP appends "E" for scientific notation literals (exp4j parses "1.5E10")
                                "EXP" -> onEvent(CalculatorEvent.DigitPressed("E"))
                                "=" -> onEvent(CalculatorEvent.EqualsPressed)
                                in listOf("÷", "x", "-", "+") -> onEvent(CalculatorEvent.OperatorPressed(label))
                                else -> onEvent(CalculatorEvent.DigitPressed(label))
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        icon = if (label == "DEL") {
                            {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}
