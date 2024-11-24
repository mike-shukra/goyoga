package ru.yogago.goyoga.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonCornerRadius

data class ButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val cornerRadius: Dp
)

@Composable
fun getButtonColors(isEnabled: Boolean): ButtonStyle {
    return ButtonStyle(
        backgroundColor = if (isEnabled) ButtonActiveColor else ButtonInactiveColor,
        contentColor = ButtonContentColor,
        cornerRadius = ButtonCornerRadius
    )
}