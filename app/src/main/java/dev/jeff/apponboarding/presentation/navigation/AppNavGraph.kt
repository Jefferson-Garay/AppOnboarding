package dev.jeff.apponboarding.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.ActividadRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import dev.jeff.apponboarding.presentation.actividad.*
import dev.jeff.apponboarding.presentation.auth.LoginScreen
import dev.jeff.apponboarding.presentation.auth.LoginState
import dev.jeff.apponboarding.presentation.auth.LoginViewModel
import dev.jeff.apponboarding.presentation.home.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // ViewModels
    val loginViewModel = remember { LoginViewModel(UsuarioRepository()) }
    val actividadViewModel = remember { ActividadViewModel(ActividadRepository()) }

    // Estado del usuario actual
    var currentUser by remember { mutableStateOf<UsuarioModel?>(null) }

    // Observar estado de login
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            currentUser = (loginState as LoginState.Success).user as? UsuarioModel
        }
    }

    NavHost(navController = navController, startDestination = "home") {

        // Pantalla de Login
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

        // Pantalla de Home
        composable("home") {
            HomeScreen(
                usuario = currentUser,
                onNavigateToActividades = {
                    navController.navigate("actividades")
                },
                onLogout = {
                    currentUser = null
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de lista de actividades
        composable("actividades") {
            ActividadesListScreen(
                viewModel = actividadViewModel,
                usuarioRef = currentUser?.id?.toString() ?: "",
                onNavigateToCreate = {
                    navController.navigate("actividades/create")
                },
                onNavigateToDetail = { actividadId ->
                    navController.navigate("actividades/detail/$actividadId")
                }
            )
        }

        // Pantalla de crear actividad
        composable("actividades/create") {
            CreateActividadScreen(
                viewModel = actividadViewModel,
                usuarioRef = currentUser?.id?.toString() ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de detalle de actividad
        composable(
            route = "actividades/detail/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.StringType })
        ) { backStackEntry ->
            val actividadId = backStackEntry.arguments?.getString("actividadId") ?: ""
            ActividadDetailScreen(
                actividadId = actividadId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    // Por implementar: navegar a editar
                    navController.popBackStack()
                }
            )
        }
    }
}