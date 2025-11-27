package dev.jeff.apponboarding

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import dev.jeff.apponboarding.presentation.navigation.AppNavGraph
import dev.jeff.apponboarding.ui.theme.AppOnboardingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado global del tema oscuro
            var isDarkTheme by remember { mutableStateOf(false) }
            // Estado global de notificaciones (simulado por ahora)
            var areNotificationsEnabled by remember { mutableStateOf(true) }
            val context = LocalContext.current
            
            AppOnboardingTheme(darkTheme = isDarkTheme) {
                AppNavGraph(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                    areNotificationsEnabled = areNotificationsEnabled,
                    onToggleNotifications = {
                        areNotificationsEnabled = !areNotificationsEnabled
                        val status = if (areNotificationsEnabled) "activadas" else "desactivadas"
                        Toast.makeText(context, "Notificaciones $status globalmente", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
