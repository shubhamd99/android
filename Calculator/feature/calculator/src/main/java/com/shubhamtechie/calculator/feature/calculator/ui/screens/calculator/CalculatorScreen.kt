package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shubhamtechie.calculator.core.designsystem.Neutral
import com.shubhamtechie.calculator.core.designsystem.Secondary
import com.shubhamtechie.calculator.feature.calculator.domain.model.CalculatorMode
import com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.components.*

@Composable
fun CalculatorRoute(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CalculatorScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onEvent: (CalculatorEvent) -> Unit
) {
    val displayWeight by animateFloatAsState(
        targetValue = if (state.mode == CalculatorMode.BASIC) 0.45f else 0.28f,
        animationSpec = tween(durationMillis = 300),
        label = "DisplayWeight"
    )
    val keypadWeight by animateFloatAsState(
        targetValue = if (state.mode == CalculatorMode.BASIC) 0.55f else 0.72f,
        animationSpec = tween(durationMillis = 300),
        label = "KeypadWeight"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Neutral
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            DisplayPanel(
                mode = state.mode,
                expression = state.expression,
                result = state.result,
                onToggleMode = { onEvent(CalculatorEvent.ToggleMode) },
                modifier = Modifier
                    .weight(displayWeight)
                    .statusBarsPadding()
            )

            Column(
                modifier = Modifier
                    .weight(keypadWeight)
                    .fillMaxWidth()
                    .background(Secondary)
                    .navigationBarsPadding()
            ) {
                if (state.mode == CalculatorMode.ADVANCED) {
                    ScientificControlBar(
                        isInverse = state.isInverse,
                        angleMode = state.angleMode,
                        isScientific = state.isScientificNotation,
                        onInverseToggle = { onEvent(CalculatorEvent.ToggleInverse) },
                        onAngleToggle = { onEvent(CalculatorEvent.ToggleAngleMode) },
                        onFormatToggle = { onEvent(CalculatorEvent.ToggleFormat) },
                        onClear = { onEvent(CalculatorEvent.ClearPressed) }
                    )
                }

                AnimatedContent(
                    targetState = state.mode,
                    transitionSpec = {
                        if (targetState == CalculatorMode.ADVANCED) {
                            slideInHorizontally(animationSpec = tween(250)) { it } + fadeIn() togetherWith
                                    slideOutHorizontally(animationSpec = tween(250)) { -it } + fadeOut()
                        } else {
                            slideInHorizontally(animationSpec = tween(250)) { -it } + fadeIn() togetherWith
                                    slideOutHorizontally(animationSpec = tween(250)) { it } + fadeOut()
                        }
                    },
                    label = "KeypadTransition",
                    modifier = Modifier.weight(1f)
                ) { mode ->
                    when (mode) {
                        CalculatorMode.BASIC -> BasicKeypad(onEvent = onEvent)
                        CalculatorMode.ADVANCED -> ScientificKeypad(
                            isInverse = state.isInverse,
                            onEvent = onEvent
                        )
                    }
                }
            }
        }
    }
}
