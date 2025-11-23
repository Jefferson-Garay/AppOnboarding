package dev.jeff.apponboarding.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Paleta de colores corporativa de TCS
private val DarkColorScheme = darkColorScheme(
    primary = AzulOscuro,
    secondary = VerdeExito,
    tertiary = Color.LightGray,
    background = Color(0xFF1C1B1F), // Un fondo oscuro estándar
    surface = Color(0xFF1C1B1F),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = AzulOscuro, // Color principal de la marca
    secondary = VerdeExito, // Color secundario para acentos
    tertiary = Color.Gray,

    // Colores de la UI que coinciden con los diseños
    background = FondoGris,
    surface = Color.White,
    
    onPrimary = Color.White, // Texto sobre el color primario
    onSecondary = Color.White, // Texto sobre el color secundario
    onBackground = AzulOscuro, // Color del texto principal
    onSurface = AzulOscuro, // Color del texto en superficies
)

@Composable
fun AppOnboardingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Lo desactivamos para forzar siempre los colores corporativos.
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}