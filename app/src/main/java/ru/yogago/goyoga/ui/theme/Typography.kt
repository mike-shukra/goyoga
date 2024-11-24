package ru.yogago.goyoga.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

val MyAppTypography = Typography(
    defaultFontFamily = FontFamily.Default,
    h1 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 30.sp),
    h2 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp),
    body1 = TextStyle(fontSize = 20.sp),
    body2 = TextStyle(fontSize = 22.sp),
    button = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    )
)