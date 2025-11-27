package dev.jeff.apponboarding.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores oscuros adaptado a la identidad corporativa
private val DarkColorScheme = darkColorScheme(
    primary = CorporateBlueLight, // Azul más claro para resaltar en oscuro
    onPrimary = Color.White,
    primaryContainer = CorporateBlue,
    onPrimaryContainer = Color.White,
    secondary = CorporateBlue,
    onSecondary = Color.White,
    background = Color(0xFF121212), // Fondo oscuro estándar
    surface = Color(0xFF1E1E1E), // Superficie oscura
    onBackground = Color.White,
    onSurface = Color.White
)

// Esquema de colores claros oficial
private val LightColorScheme = lightColorScheme(
    primary = CorporateBlue,
    onPrimary = Color.White,
    primaryContainer = CorporateBlueDark,
    onPrimaryContainer = Color.White,
    secondary = CorporateBlueLight,
    onSecondary = Color.White,
    background = CorporateGrayLight, // Gris muy claro institucional
    surface = Color.White,
    onBackground = CorporateBlack,
    onSurface = CorporateBlack,
    surfaceVariant = CorporateGrayLight,
    onSurfaceVariant = CorporateGrayDark
)

@Composable
fun AppOnboardingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivamos dynamicColor por defecto para forzar identidad corporativa
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Si está en tema oscuro, la barra de estado es transparente o oscura
            // Aquí le decimos al sistema si usar iconos claros u oscuros
            // (isAppearanceLightStatusBars = !darkTheme) -> Si es oscuro, false (iconos claros)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
