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
import dev.jeff.apponboarding.presentation.configuracion.ConfiguracionScreen
// Archivo: AppNavGraph.kt

// ...
import dev.jeff.apponboarding.presentation.home.HomeScreen
import dev.jeff.apponboarding.presentation.mensaje.ProgramarMensajesScreen
import dev.jeff.apponboarding.presentation.perfil.MiInformacionScreen
import dev.jeff.apponboarding.presentation.recurso.*
import dev.jeff.apponboarding.presentation.rol.*
import dev.jeff.apponboarding.presentation.supervisor.MiSupervisorScreen
import dev.jeff.apponboarding.presentation.usuario.UsuarioDetailScreen
import dev.jeff.apponboarding.presentation.usuario.UsuarioViewModel
import dev.jeff.apponboarding.presentation.usuario.UsuariosListScreen
import dev.jeff.apponboarding.data.remote.actividad.ActividadService
import dev.jeff.apponboarding.data.remote.RetrofitInstance // Asumiendo que existe
import dev.jeff.apponboarding.presentation.dashboard.DashboardScreen
import dev.jeff.apponboarding.presentation.dashboard.DashboardViewModel

// IMPLEMENTACIÓN CRUCIAL: Retorna la instancia del servicio API
fun provideActividadService(): ActividadService {
    return RetrofitInstance.actividadApi
}


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // --- INSTANCIAS DE SERVICIO ---
    val actividadService = remember { provideActividadService() }

    // --- INSTANCIAS DE REPOSITORIOS (Corregido para constructores vacíos) ---
    val usuarioRepository = remember { UsuarioRepository() } // Usado para login, getUsuarios
    val actividadRepository = remember { ActividadRepository() } // No tiene constructor
    val recursoRepository = remember { RecursoRepository() }
    val rolRepository = remember { RolRepository() }
    val chatRepository = remember { ChatRepository() }

    // [1] INICIALIZACIÓN DEL DASHBOARD (CORREGIDA): Pasa el servicio de API Y el Repositorio de Usuarios
    val dashboardRepository = remember { DashboardRepository(actividadService, usuarioRepository) }

    // --- INSTANCIAS DE VIEWMODEL ---
    val loginViewModel = remember { LoginViewModel(usuarioRepository) }
    val actividadViewModel = remember { ActividadViewModel(actividadRepository) }
    val recursoViewModel = remember { RecursoViewModel(recursoRepository) }
    val rolViewModel = remember { RolViewModel(rolRepository) }
    val chatViewModel = remember { ChatViewModel(chatRepository) }
    val usuarioViewModel = remember { UsuarioViewModel() }
    val dashboardViewModel = remember { DashboardViewModel(dashboardRepository) } // Usa el repo corregido

    var currentUser by remember { mutableStateOf<UsuarioModel?>(null) }
    var isDarkTheme by remember { mutableStateOf(false) }

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
                // [2] NAVEGACIÓN A DASHBOARD
                onNavigateToDashboard = { navController.navigate("dashboard") },
                onNavigateToActividades = { navController.navigate("actividades") },
                onNavigateToRecursos = { navController.navigate("recursos") },
                onNavigateToRoles = { navController.navigate("roles") },
                onNavigateToChat = { navController.navigate("chat") },
                onNavigateToSupervisor = { navController.navigate("supervisor") },
                onNavigateToAyuda = { navController.navigate("ayuda") },
                onNavigateToMensajes = { navController.navigate("mensajes") },
                onNavigateToConfiguracion = { navController.navigate("configuracion") },
                onNavigateToPerfil = { navController.navigate("perfil") },
                onNavigateToUsuarios = { navController.navigate("usuarios") },
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

        // [3] RUTA DEL DASHBOARD
        composable("dashboard") {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // === RUTA DE CONFIGURACIÓN ===

        composable("configuracion") {
            ConfiguracionScreen(
                usuario = currentUser,
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = { isDarkTheme = it },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("perfil") {
            MiInformacionScreen(
                usuario = currentUser,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToActividades = {
                    navController.navigate("actividades")
                },
                onNavigateToSupervisor = {
                    navController.navigate("supervisor")
                }
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