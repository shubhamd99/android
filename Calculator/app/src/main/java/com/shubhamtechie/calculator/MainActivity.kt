package com.shubhamtechie.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shubhamtechie.calculator.core.designsystem.Primary
import com.shubhamtechie.calculator.core.designsystem.ThermalIndustrialTheme
import com.shubhamtechie.calculator.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Primary.toArgb())
        )

        setContent {
            ThermalIndustrialTheme {
                AppNavHost()
            }
        }
    }
}
