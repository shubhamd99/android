package com.shubhamtechie.calculator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.CalculatorRoute

@Composable
fun AppNavHost() {
    val backStack = rememberNavBackStack(Screen.Calculator)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Calculator> {
                CalculatorRoute()
            }
        }
    )
}
