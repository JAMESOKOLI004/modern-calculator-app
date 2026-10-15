package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OrangeOperator,
    onPrimary = TextWhite,
    secondary = OrangeOperatorActive,
    onSecondary = TextWhite,
    tertiary = FunctionLightGray,
    onTertiary = TextWhite,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = NumberDarkGray,
    onSurface = TextWhite,
    surfaceVariant = FunctionLightGray,
    onSurfaceVariant = TextMutedGray,
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeOperator,
    onPrimary = TextWhite,
    secondary = OrangeOperatorActive,
    onSecondary = TextWhite,
    tertiary = LightFunctionBg,
    onTertiary = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightFunctionBg,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
