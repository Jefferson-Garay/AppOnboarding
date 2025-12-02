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
import dev.jeff.apponboarding.presentation.home.HomeEmpleadoScreen  // ⭐ NUEVO
import dev.jeff.apponboarding.presentation.home.HomeAdminScreen     // ⭐ NUEVO
import dev.jeff.apponboarding.presentation.mensaje.ProgramarMensajesScreen
import dev.jeff.apponboarding.presentation.recurso.*
import dev.jeff.apponboarding.presentation.rol.*
import dev.jeff.apponboarding.presentation.supervisor.MiSupervisorScreen
import dev.jeff.apponboarding.presentation.usuario.UsuarioDetailScreen
import dev.jeff.apponboarding.presentation.usuario.UsuarioViewModel
import dev.jeff.apponboarding.presentation.usuario.UsuariosListScreen
import dev.jeff.apponboarding.presentation.configuracion.ConfiguracionScreen
import dev.jeff.apponboarding.presentation.perfil.MiInformacionScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val loginViewModel = remember { LoginViewModel(UsuarioRepository()) }
    val actividadViewModel = remember { ActividadViewModel(ActividadRepository()) }
    val recursoViewModel = remember { RecursoViewModel(RecursoRepository()) }
    val rolViewModel = remember { RolViewModel(RolRepository()) }
    val chatViewModel = remember { ChatViewModel(ChatRepository()) }
    val usuarioViewModel = remember { UsuarioViewModel() }

    var currentUser by remember { mutableStateOf<UsuarioModel?>(null) }
    var isDarkTheme by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsState()

    // ⭐ ACTUALIZADO: Actualizar currentUser cuando hay éxito en login
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            currentUser = (loginState as LoginState.Success).user as? UsuarioModel
        }
    }

    // ⭐ NUEVO: Función helper para verificar si es admin
    fun esAdministrador(usuario: UsuarioModel?): Boolean {
        val rolId = usuario?.rolRef ?: return false
        return rolId == "6913adbcca79acfd93858d5c" ||
                rolId.contains("admin", ignoreCase = true)
    }

    NavHost(navController = navController, startDestination = "login") {

        // ⭐ ACTUALIZADO: Login con navegación basada en rol
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccessEmpleado = {
                    navController.navigate("home_empleado") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccessAdmin = {
                    navController.navigate("home_admin") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // ⭐ NUEVO: Home para EMPLEADO
        composable("home_empleado") {
            HomeEmpleadoScreen(
                usuario = currentUser,
                actividadViewModel = actividadViewModel,
                onNavigateToActividades = { navController.navigate("actividades") },
                onNavigateToRecursos = { navController.navigate("recursos") },
                onNavigateToChat = { navController.navigate("chat") },
                onNavigateToSupervisor = { navController.navigate("supervisor") },
                onNavigateToAyuda = { navController.navigate("ayuda") },
                onNavigateToConfiguracion = { navController.navigate("configuracion") },
                onNavigateToPerfil = { navController.navigate("perfil") },
                onNavigateToActividadDetail = { actividadId ->
                    navController.navigate("actividades/detail/$actividadId")
                },
                onLogout = {
                    currentUser = null
                    loginViewModel.resetLoginState()
                    navController.navigate("login") {
                        popUpTo("home_empleado") { inclusive = true }
                    }
                }
            )
        }

        // ⭐ NUEVO: Home para ADMINISTRADOR
        composable("home_admin") {
            HomeAdminScreen(
                usuario = currentUser,
                usuarioViewModel = usuarioViewModel,
                onNavigateToMensajes = { navController.navigate("mensajes") },
                onNavigateToRoles = { navController.navigate("roles") },
                onNavigateToUsuarios = { navController.navigate("usuarios") },
                onNavigateToConfiguracion = { navController.navigate("configuracion") },
                onNavigateToPerfil = { navController.navigate("perfil") },
                onLogout = {
                    currentUser = null
                    loginViewModel.resetLoginState()
                    navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                }
            )
        }

        // ===== PANTALLAS COMPARTIDAS =====

        composable("configuracion") {
            ConfiguracionScreen(
                usuario = currentUser,
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = { isDarkTheme = it },
                onNavigateBack = {
                    // ⭐ Volver al home correcto según rol
                    if (esAdministrador(currentUser)) {
                        navController.navigate("home_admin") {
                            popUpTo("configuracion") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home_empleado") {
                            popUpTo("configuracion") { inclusive = true }
                        }
                    }
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

        // ===== PANTALLAS SOLO ADMIN =====

        composable("mensajes") {
            ProgramarMensajesScreen(
                usuarioRef = currentUser?.id?.toString() ?: "",
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

        // ===== PANTALLAS SOLO EMPLEADO =====

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
    }
}