package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shubhamtechie.calculator.core.designsystem.*
import com.shubhamtechie.calculator.feature.calculator.domain.model.CalculatorMode

@Composable
fun DisplayPanel(
    mode: CalculatorMode,
    expression: String,
    result: String,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary)
            .padding(24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        ModeChip(
            mode = mode,
            onClick = onToggleMode,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = expression,
            style = CalculatorTypography.bodyMedium,
            color = MutedWhite,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        AnimatedContent(
            targetState = result,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
            },
            label = "ResultFade"
        ) { displayResult ->
            Text(
                text = displayResult,
                style = CalculatorTypography.headlineLarge,
                color = White,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = "Result: $displayResult"
                    }
            )
        }
    }
}

@Composable
fun ModeChip(
    mode: CalculatorMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (mode == CalculatorMode.BASIC) "BASIC MODE" else "ADVANCED MODE",
                style = CalculatorTypography.labelMedium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Toggle mode",
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
