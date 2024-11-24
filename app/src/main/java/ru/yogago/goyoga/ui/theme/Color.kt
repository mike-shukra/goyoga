package ru.yogago.goyoga.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// Цвета для кнопки
val ButtonActiveColor = Color(0xFFDC8628)
val ButtonInactiveColor = Color(0xFFB7B7B7)
val ButtonContentColor = Color(0xFFFAFAFA)
val LoadingIndicatorColor = Color(0xFFFAFAFA)

val TextFieldPlaceholderColor = Color(0xFF757575)
val TextFieldFocusedIndicatorColor = Color(0xFF757575)
val TextFieldUnfocusedIndicatorColor = Color(0xFF757575)
val TextFieldErrorColor = Color(0xFFFF0000)
val TextFieldTextColor = Color(0xFFFAFAFA)

val primary = Color(0xFF303030)
val primaryLite = Color(0xFF555555)
val primaryMiddle = Color(0xFF232323)
val primaryDark = Color(0xFF151515)
val accent = Color(0xFFDC8628)
val accentDark = Color(0xFFDC8628)
val whiteMiddle = Color(0xFFB7B7B7)
val whiteLite = Color(0xFFFAFAFA)

// Цвета для светлой темы
val LightThemeColors: Colors = lightColors(
    primary = Color(0xFFDC8628),
    primaryVariant = Color(0xFFDC8628),
    secondary = Color(0xFFB7B7B7),
    background = Color(0xFF303030),
    surface = Color(0xFFFAFAFA),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFF555555),
    onSurface = Color(0xFF757575)
)

// Цвета для темной темы
val DarkThemeColors: Colors = darkColors(
    primary = Color(0xFFDC8628),
    primaryVariant = Color(0xFFDC8628),
    secondary = Color(0xFFB7B7B7),
    background = Color(0xFF303030),
    surface = Color(0xFFFAFAFA),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFF555555),
    onSurface = Color(0xFF757575)
)