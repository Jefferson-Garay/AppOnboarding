package dev.jeff.apponboarding.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.ActividadRepository
import dev.jeff.apponboarding.data.repository.ChatRepository
import dev.jeff.apponboarding.data.repository.RecursoRepository
import dev.jeff.apponboarding.data.repository.RolRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import dev.jeff.apponboarding.presentation.actividad.*
import dev.jeff.apponboarding.presentation.auth.LoginScreen
import dev.jeff.apponboarding.presentation.auth.LoginState
import dev.jeff.apponboarding.presentation.auth.LoginViewModel
import dev.jeff.apponboarding.presentation.chat.*
import dev.jeff.apponboarding.presentation.home.HomeScreen
import dev.jeff.apponboarding.presentation.recurso.*
import dev.jeff.apponboarding.presentation.rol.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // ViewModels
    val loginViewModel = remember { LoginViewModel(UsuarioRepository()) }
    val actividadViewModel = remember { ActividadViewModel(ActividadRepository()) }
    val recursoViewModel = remember { RecursoViewModel(RecursoRepository()) }
    val rolViewModel = remember { RolViewModel(RolRepository()) }
    val chatViewModel = remember { ChatViewModel(ChatRepository()) }

    // Estado del usuario actual
    var currentUser by remember { mutableStateOf<UsuarioModel?>(null) }

    // Observar estado de login
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            currentUser = (loginState as LoginState.Success).user as? UsuarioModel
        }
    }

    NavHost(navController = navController, startDestination = "login") {

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
                actividadViewModel = actividadViewModel,
                onNavigateToActividades = {
                    navController.navigate("actividades")
                },
                onNavigateToRecursos = {
                    navController.navigate("recursos")
                },
                onNavigateToRoles = {
                    navController.navigate("roles")
                },
                onNavigateToChat = {
                    navController.navigate("chat")
                },
                onNavigateToActividadDetail = { actividadId ->
                    navController.navigate("actividades/detail/$actividadId")
                },
                onLogout = {
                    currentUser = null
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // === RUTA DE CHAT ===

        composable("chat") {
            ChatScreen(
                viewModel = chatViewModel,
                usuario = currentUser,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // === RUTAS DE ACTIVIDADES ===

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

        composable("actividades/create") {
            CreateActividadScreen(
                viewModel = actividadViewModel,
                usuarioRef = currentUser?.id?.toString() ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

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

        composable("recursos/create") {
            CreateRecursoScreen(
                viewModel = recursoViewModel,
                adminRef = currentUser?.id?.toString() ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

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

        // === RUTAS DE ROLES ===

        composable("roles") {
            RolesListScreen(
                viewModel = rolViewModel,
                onNavigateToCreate = {
                    navController.navigate("roles/create")
                },
                onNavigateToDetail = { rolId ->
                    navController.navigate("roles/detail/$rolId")
                }
            )
        }

        composable("roles/create") {
            CreateRolScreen(
                viewModel = rolViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "roles/detail/{rolId}",
            arguments = listOf(navArgument("rolId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rolId = backStackEntry.arguments?.getString("rolId") ?: ""
            RolDetailScreen(
                rolId = rolId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.popBackStack()
                }
            )
        }
    }
}