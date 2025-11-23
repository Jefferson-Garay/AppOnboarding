package dev.jeff.apponboarding.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.ActividadRepository
import dev.jeff.apponboarding.data.repository.RecursoRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import dev.jeff.apponboarding.presentation.actividad.*
import dev.jeff.apponboarding.presentation.auth.LoginScreen
import dev.jeff.apponboarding.presentation.auth.LoginState
import dev.jeff.apponboarding.presentation.auth.LoginViewModel
import dev.jeff.apponboarding.presentation.home.HomeScreen
import dev.jeff.apponboarding.presentation.recurso.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // ViewModels
    val loginViewModel = remember { LoginViewModel(UsuarioRepository()) }
    val actividadViewModel = remember { ActividadViewModel(ActividadRepository()) }
    val recursoViewModel = remember { RecursoViewModel(RecursoRepository()) }

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
                onNavigateToRecursos = {
                    navController.navigate("recursos")
                },
                onLogout = {
                    currentUser = null
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // === RUTAS DE ACTIVIDADES ===

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
                    navController.popBackStack()
                }
            )
        }

        // === RUTAS DE RECURSOS ===

        // Pantalla de lista de recursos
        composable("recursos") {
            RecursosListScreen(
                viewModel = recursoViewModel,
                onNavigateToCreate = {
                    navController.navigate("recursos/create")
                },
                onNavigateToDetail = { recursoId ->
                    navController.navigate("recursos/detail/$recursoId")
                }
            )
        }

        // Pantalla de crear recurso
        composable("recursos/create") {
            CreateRecursoScreen(
                viewModel = recursoViewModel,
                adminRef = currentUser?.id?.toString() ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de detalle de recurso
        composable(
            route = "recursos/detail/{recursoId}",
            arguments = listOf(navArgument("recursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId") ?: ""
            RecursoDetailScreen(
                recursoId = recursoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}