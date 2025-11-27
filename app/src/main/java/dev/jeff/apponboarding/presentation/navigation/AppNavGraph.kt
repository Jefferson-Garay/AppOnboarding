package dev.jeff.apponboarding.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.ActividadRepository
import dev.jeff.apponboarding.data.repository.ChatRepository
import dev.jeff.apponboarding.data.repository.HistoryRepository
import dev.jeff.apponboarding.data.repository.RecursoRepository
import dev.jeff.apponboarding.data.repository.RolRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import dev.jeff.apponboarding.data.repository.*
import dev.jeff.apponboarding.presentation.actividad.*
import dev.jeff.apponboarding.presentation.auth.LoginScreen
import dev.jeff.apponboarding.presentation.auth.LoginState
import dev.jeff.apponboarding.presentation.auth.LoginViewModel
import dev.jeff.apponboarding.presentation.ayuda.AyudaScreen
import dev.jeff.apponboarding.presentation.chat.*
import dev.jeff.apponboarding.presentation.configuracion.ConfiguracionScreen
import dev.jeff.apponboarding.presentation.configuracion.ConfiguracionViewModel
import dev.jeff.apponboarding.presentation.history.HistoryScreen
import dev.jeff.apponboarding.presentation.history.HistoryViewModel
import dev.jeff.apponboarding.presentation.home.HomeScreen
import dev.jeff.apponboarding.presentation.mensaje.ProgramarMensajesScreen
import dev.jeff.apponboarding.presentation.recurso.*
import dev.jeff.apponboarding.presentation.rol.*
import dev.jeff.apponboarding.presentation.supervisor.MiSupervisorScreen
import dev.jeff.apponboarding.presentation.usuario.UsuarioDetailScreen
import dev.jeff.apponboarding.presentation.usuario.UsuarioViewModel
import dev.jeff.apponboarding.presentation.usuario.UsuariosListScreen

@Composable
fun AppNavGraph(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    areNotificationsEnabled: Boolean = true,
    onToggleNotifications: () -> Unit = {}
) {
    val navController = rememberNavController()

    val loginViewModel = remember { LoginViewModel(UsuarioRepository()) }
    val actividadViewModel = remember { ActividadViewModel(ActividadRepository()) }
    val recursoViewModel = remember { RecursoViewModel(RecursoRepository()) }
    val rolViewModel = remember { RolViewModel(RolRepository()) }
    val chatViewModel = remember { ChatViewModel(ChatRepository()) }
    val historyViewModel = remember { HistoryViewModel(HistoryRepository()) }
    val configuracionViewModel = remember { ConfiguracionViewModel(UsuarioRepository()) }

    // Estado del usuario actual
    // Se inicia en null para que obligue a pasar por login
    val usuarioViewModel = remember { UsuarioViewModel() }

    var currentUser by remember { mutableStateOf<UsuarioModel?>(null) }
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            currentUser = (loginState as LoginState.Success).user as? UsuarioModel
            // Navegar a home cuando el login es exitoso
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Función auxiliar para formatear el ID de manera segura
    fun formatId(id: Any?): String {
        return when (id) {
            is Double -> id.toLong().toString() // Convierte 15.0 a "15"
            is Float -> id.toLong().toString()
            else -> id?.toString() ?: ""
        }
    }

    // CAMBIO: startDestination restaurado a "login" para forzar autenticación
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // La navegación ahora se maneja en el LaunchedEffect al observar el estado
                }
            )
        }

        composable("home") {
            HomeScreen(
                usuario = currentUser,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
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
                actividadViewModel = actividadViewModel,
                onNavigateToActividades = { navController.navigate("actividades") },
                onNavigateToRecursos = { navController.navigate("recursos") },
                onNavigateToRoles = { navController.navigate("roles") },
                onNavigateToChat = { navController.navigate("chat") },
                onNavigateToSupervisor = { navController.navigate("supervisor") },
                onNavigateToAyuda = { navController.navigate("ayuda") },
                onNavigateToMensajes = { navController.navigate("mensajes") },
                onNavigateToUsuarios = { navController.navigate("usuarios") },
                onNavigateToActividadDetail = { actividadId ->
                    navController.navigate("actividades/detail/$actividadId")
                },
                onNavigateToHistory = {
                    navController.navigate("history")
                },
                onNavigateToConfiguracion = {
                    navController.navigate("configuracion")
                },
                onLogout = {
                    currentUser = null
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        
        // === AYUDA ELIMINADA ===
        /*
        composable("ayuda") {
            AyudaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        */

        composable("configuracion") {
            // Usamos formatId para asegurar que enviamos "15" y no "15.0"
            val safeId = formatId(currentUser?.id)
            
            ConfiguracionScreen(
                viewModel = configuracionViewModel,
                usuarioId = safeId,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                areNotificationsEnabled = areNotificationsEnabled,
                onToggleNotifications = onToggleNotifications,
                onNavigateBack = { navController.popBackStack() }
            )
        }


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

        // === RUTA DE HISTORIAL DE CHAT ===

        composable("history") {
            val isAdmin = currentUser?.rolRef != null // Lógica simple, ajustar si es necesario
            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                isAdmin = isAdmin
            )
        }

        // === RUTAS DE ACTIVIDADES ===

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
                usuario = currentUser,
                onNavigateToCreate = {
                    navController.navigate("recursos/create")
                },
                onNavigateToDetail = { recursoId ->
                    navController.navigate("recursos/detail/$recursoId")
                }
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

        composable("usuarios") {
            UsuariosListScreen(
                viewModel = usuarioViewModel,
                onNavigateToCreate = { navController.navigate("usuarios/create") },
                onNavigateToEdit = { id -> navController.navigate("usuarios/edit/$id") }
            )
        }

        composable("usuarios/create") {
            UsuarioDetailScreen(
                viewModel = usuarioViewModel,
                usuarioId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "usuarios/edit/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            UsuarioDetailScreen(
                viewModel = usuarioViewModel,
                usuarioId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
