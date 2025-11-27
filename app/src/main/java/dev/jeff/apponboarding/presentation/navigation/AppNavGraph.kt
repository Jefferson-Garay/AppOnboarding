package dev.jeff.apponboarding.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.*
import dev.jeff.apponboarding.presentation.actividad.*
import dev.jeff.apponboarding.presentation.auth.LoginScreen
import dev.jeff.apponboarding.presentation.auth.LoginState
import dev.jeff.apponboarding.presentation.auth.LoginViewModel
import dev.jeff.apponboarding.presentation.ayuda.AyudaScreen
import dev.jeff.apponboarding.presentation.chat.*
import dev.jeff.apponboarding.presentation.home.HomeScreen
import dev.jeff.apponboarding.presentation.mensaje.ProgramarMensajesScreen
import dev.jeff.apponboarding.presentation.recurso.*
import dev.jeff.apponboarding.presentation.rol.*
import dev.jeff.apponboarding.presentation.supervisor.MiSupervisorScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // ViewModels
    val loginViewModel = remember { LoginViewModel(UsuarioRepository()) }
    val actividadViewModel = remember { ActividadViewModel(ActividadRepository()) }
    val recursoViewModel = remember { RecursoViewModel(RecursoRepository()) }
    val rolViewModel = remember { RolViewModel(RolRepository()) }
    val chatViewModel = remember { ChatViewModel(ChatRepository()) }

    var currentUser by remember { mutableStateOf<UsuarioModel?>(null) }
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            currentUser = (loginState as LoginState.Success).user as? UsuarioModel
        }
    }

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
            HomeScreen(
                usuario = currentUser,
                actividadViewModel = actividadViewModel,
                onNavigateToActividades = { navController.navigate("actividades") },
                onNavigateToRecursos = { navController.navigate("recursos") },
                onNavigateToRoles = { navController.navigate("roles") },
                onNavigateToChat = { navController.navigate("chat") },
                onNavigateToSupervisor = { navController.navigate("supervisor") },
                onNavigateToAyuda = { navController.navigate("ayuda") },
                onNavigateToMensajes = { navController.navigate("mensajes") }, // US 10
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

        // RUTA NUEVA US 10
        composable("mensajes") {
            ProgramarMensajesScreen(
                usuarioRef = currentUser?.id?.toString() ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("supervisor") {
            MiSupervisorScreen(
                usuarioActual = currentUser,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("ayuda") {
            AyudaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSupervisor = { navController.navigate("supervisor") },
                onNavigateToActividades = { navController.navigate("actividades") },
                onNavigateToRecursos = { navController.navigate("recursos") },
                onNavigateToChat = { navController.navigate("chat") }
            )
        }

        composable("chat") {
            ChatScreen(
                viewModel = chatViewModel,
                usuario = currentUser,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("actividades") {
            ActividadesListScreen(
                viewModel = actividadViewModel,
                usuarioRef = currentUser?.id?.toString() ?: "",
                onNavigateToCreate = { navController.navigate("actividades/create") },
                onNavigateToDetail = { id -> navController.navigate("actividades/detail/$id") }
            )
        }

        composable("actividades/create") {
            CreateActividadScreen(
                viewModel = actividadViewModel,
                usuarioRef = currentUser?.id?.toString() ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "actividades/detail/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.StringType })
        ) { backStackEntry ->
            val actividadId = backStackEntry.arguments?.getString("actividadId") ?: ""
            ActividadDetailScreen(
                actividadId = actividadId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.popBackStack() }
            )
        }

        composable("recursos") {
            RecursosListScreen(
                viewModel = recursoViewModel,
                onNavigateToCreate = { navController.navigate("recursos/create") },
                onNavigateToDetail = { id -> navController.navigate("recursos/detail/$id") }
            )
        }

        composable("recursos/create") {
            CreateRecursoScreen(
                viewModel = recursoViewModel,
                adminRef = currentUser?.id?.toString() ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "recursos/detail/{recursoId}",
            arguments = listOf(navArgument("recursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId") ?: ""
            RecursoDetailScreen(
                recursoId = recursoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("roles") {
            RolesListScreen(
                viewModel = rolViewModel,
                onNavigateToCreate = { navController.navigate("roles/create") },
                onNavigateToDetail = { id -> navController.navigate("roles/detail/$id") }
            )
        }

        composable("roles/create") {
            CreateRolScreen(
                viewModel = rolViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "roles/detail/{rolId}",
            arguments = listOf(navArgument("rolId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rolId = backStackEntry.arguments?.getString("rolId") ?: ""
            RolDetailScreen(
                rolId = rolId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.popBackStack() }
            )
        }
    }
}