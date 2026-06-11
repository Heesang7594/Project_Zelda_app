package com.example.thelegendofzelda.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

private val DarkColorScheme = darkColorScheme(
    primary = ZeldaGreenLight,
    secondary = ZeldaGreenDark,
    tertiary = ZeldaGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = LightSurface,
    onBackground = LightSurface,
    onSurface = LightSurface
)

private val LightColorScheme = lightColorScheme(
    primary = ZeldaGreen,
    secondary = ZeldaGreenDark,
    tertiary = ZeldaGreenLight,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightSurface,
    onBackground = DarkBackground,
    onSurface = DarkBackground
)

class ThemeState(val isDark: Boolean, val toggleTheme: () -> Unit)
val LocalThemeState = compositionLocalOf<ThemeState> { error("No ThemeState provided") }

@Composable
fun TheLegendOfZeldaTheme(
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDarkState = remember { mutableStateOf(systemDark) }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkState.value) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkState.value -> DarkColorScheme
        else -> LightColorScheme
    }

    val themeState = remember(isDarkState.value) {
        ThemeState(isDarkState.value) { isDarkState.value = !isDarkState.value }
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalThemeState provides themeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}