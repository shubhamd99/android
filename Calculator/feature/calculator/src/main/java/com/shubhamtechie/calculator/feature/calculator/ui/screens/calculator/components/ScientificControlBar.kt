package com.shubhamtechie.calculator.feature.calculator.ui.screens.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubhamtechie.calculator.core.designsystem.CalculatorTypography
import com.shubhamtechie.calculator.core.designsystem.OperatorVariant
import com.shubhamtechie.calculator.core.designsystem.White
import com.shubhamtechie.calculator.feature.calculator.domain.model.AngleMode

@Composable
fun ScientificControlBar(
    isInverse: Boolean,
    angleMode: AngleMode,
    isScientific: Boolean,
    onInverseToggle: () -> Unit,
    onAngleToggle: () -> Unit,
    onFormatToggle: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlChip(
            label = "INV",
            isActive = isInverse,
            onClick = onInverseToggle,
            modifier = Modifier.weight(1f)
        )
        ControlChip(
            label = if (angleMode == AngleMode.DEG) "DEG" else "RAD",
            isActive = false,
            onClick = onAngleToggle,
            modifier = Modifier.weight(1f)
        )
        ControlChip(
            label = "F-E",
            isActive = isScientific,
            onClick = onFormatToggle,
            modifier = Modifier.weight(1f)
        )
        ControlChip(
            label = "CLEAN",
            isActive = false,
            onClick = onClear,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ControlChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OperatorVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isActive) OperatorVariant else Color.Transparent,
            contentColor = White
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = label,
            style = CalculatorTypography.labelMedium,
            fontSize = 12.sp
        )
    }
}
