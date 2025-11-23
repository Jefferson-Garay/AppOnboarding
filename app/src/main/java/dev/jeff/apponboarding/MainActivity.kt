package dev.jeff.apponboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// 👇 Este import busca AppNavGraph en el paquete correcto que definimos antes
import dev.jeff.apponboarding.presentation.navigation.AppNavGraph
import dev.jeff.apponboarding.ui.theme.AppOnboardingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Envuelve tu navegación con el tema de la app (Buena práctica)
            AppOnboardingTheme {
                AppNavGraph()
            }
        }
    }
}