package com.shubhamtechie.calculator.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ThermalIndustrialTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Primary,
            secondary = Secondary,
            background = Neutral,
            surface = Secondary
        ),
        typography = CalculatorTypography,
        content = content
    )
}
