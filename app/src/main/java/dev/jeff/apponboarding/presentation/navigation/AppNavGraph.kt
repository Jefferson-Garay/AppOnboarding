package dev.jeff.apponboarding.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import dev.jeff.apponboarding.presentation.auth.LoginScreen
import dev.jeff.apponboarding.presentation.auth.LoginViewModel
import dev.jeff.apponboarding.presentation.home.HomeScreen
import dev.jeff.apponboarding.data.repository.UsuarioRepository

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val loginViewModel = LoginViewModel(UsuarioRepository())

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}
