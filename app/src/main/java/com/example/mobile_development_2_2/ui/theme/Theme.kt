package com.example.mobile_development_2_2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mobile_development_2_2.data.Lang
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Primary,
    onPrimary = Color(0xFFFFFFFF),
)

private val LightColorPalette = lightColors(
    primary = Primary,
    surface = Surface,
    background = Background,
)

private val DarkColorBlindPalette = darkColors(
    primary = Color(0xFF7D7D7D),
    onPrimary = Color(0xFFFFFFFF),

    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF141414),
    onSurface = Color(0xFFFFFFFF)
)

private val LightColorBlindPalette = lightColors(
    primary = Color(0xFF7D7D7D),
    onPrimary = Color(0xFF000000),


    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),

    surface = Color(0xFFEBEBEB),
    onSurface = Color(0xFF000000),
)

@Composable
fun MobileDevelopment2_2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorBlind: Boolean = Lang.colorblind,
    content: @Composable () -> Unit
) {
    val colors = if (colorBlind) {
        if (darkTheme) {
            DarkColorBlindPalette
        } else {
            LightColorBlindPalette
        }
    }
    else if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(
            color = colors.primary
        )

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}