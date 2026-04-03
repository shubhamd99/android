package com.shubhamtechie.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.shubhamtechie.calculator.core.designsystem.Primary
import com.shubhamtechie.calculator.core.designsystem.ThermalIndustrialTheme
import com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.CalculatorRoute
import com.shubhamtechie.calculator.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val orangeColor = Primary.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(orangeColor)
        )
        
        setContent {
            ThermalIndustrialTheme {
                AppNavHost()
            }
        }
    }
}

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
