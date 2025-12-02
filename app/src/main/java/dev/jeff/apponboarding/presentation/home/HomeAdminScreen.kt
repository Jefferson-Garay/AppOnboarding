package dev.jeff.apponboarding.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.ActividadRepository
import dev.jeff.apponboarding.presentation.actividad.ActividadViewModel
import dev.jeff.apponboarding.presentation.usuario.UsuarioViewModel
import kotlinx.coroutines.launch

// Colores consistentes
private val ColorFondoApp = Color(0xFFFFFFFF)
private val ColorCardBienvenida = Color(0xFFEBE8F2)
private val ColorCardAdmin = Color(0xFFE3F2FD)  // Azul claro para admin
private val ColorCardAccesos = Color(0xFFEBE8F2)
private val ColorAvatarAdmin = Color(0xFF1976D2)  // Azul para admin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen(
    usuario: UsuarioModel?,
    usuarioViewModel: UsuarioViewModel,
    onNavigateToMensajes: () -> Unit,
    onNavigateToRoles: () -> Unit,
    onNavigateToUsuarios: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("inicio") }

    val usuariosState by usuarioViewModel.usuariosState.collectAsState()

    // Instanciamos el ViewModel de actividades aquí para escuchar notificaciones
    val actividadViewModel = remember { ActividadViewModel(ActividadRepository()) }
    val mensajePopup by actividadViewModel.mensajeEmergente.collectAsState()

    // Cargar usuarios y verificar mensajes al iniciar
    LaunchedEffect(Unit) {
        usuarioViewModel.loadUsuarios()
        usuario?.let {
            // Cargar actividades para detectar mensajes "MSG_" dirigidos al admin
            actividadViewModel.loadActividadesByUsuario(it.id?.toString() ?: "")
        }
    }

    // Estadísticas de usuarios
    val totalUsuarios = usuariosState.size
    val usuariosActivos = usuariosState.count { it.estado?.equals("Activo", ignoreCase = true) == true }
    val usuariosInactivos = totalUsuarios - usuariosActivos

    // ⭐ MENÚ PARA ADMINISTRADOR (6 opciones)
    val menuItems = listOf(
        DrawerMenuItem("inicio", "Inicio", Icons.Outlined.Home, Icons.Filled.Home),
        DrawerMenuItem("mensajes", "Automatización", Icons.Outlined.ScheduleSend, Icons.Filled.ScheduleSend),
        DrawerMenuItem("roles", "Gestionar Roles", Icons.Outlined.Security, Icons.Filled.Security),
        DrawerMenuItem("usuarios", "Gestionar Empleados", Icons.Outlined.People, Icons.Filled.People),
        DrawerMenuItem("perfil", "Mi Información", Icons.Outlined.Person, Icons.Filled.Person),
        DrawerMenuItem("configuracion", "Configuración", Icons.Outlined.Settings, Icons.Filled.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color(0xFF1A237E)) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { scope.launch { drawerState.close() } }) {
                            Icon(Icons.Default.Close, "Cerrar", tint = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Badge de ADMIN
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Shield, "Admin", tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("ADMINISTRADOR", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ⭐ MENÚ PARA ADMIN
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title, color = Color.White) },
                        icon = { Icon(if (selectedItem == item.id) item.selectedIcon else item.icon, null, tint = Color.White) },
                        selected = selectedItem == item.id,
                        onClick = {
                            selectedItem = item.id
                            scope.launch { drawerState.close() }
                            when (item.id) {
                                "mensajes" -> onNavigateToMensajes()
                                "roles" -> onNavigateToRoles()
                                "usuarios" -> onNavigateToUsuarios()
                                "perfil" -> onNavigateToPerfil()
                                "configuracion" -> onNavigateToConfiguracion()
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color.White.copy(alpha = 0.2f),
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = Color(0xFFEF5350)) },
                    icon = { Icon(Icons.Outlined.ExitToApp, null, tint = Color(0xFFEF5350)) },
                    selected = false,
                    onClick = onLogout,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            containerColor = ColorFondoApp,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(32.dp), shape = RoundedCornerShape(4.dp), color = Color(0xFFE8DEF8)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Shield, null, modifier = Modifier.size(20.dp), tint = Color(0xFF1D1B20))
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("TCS - Admin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Panel de Administración", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menú")
                        }
                    },
                    actions = {
                        Surface(modifier = Modifier.size(32.dp).clip(CircleShape), color = ColorAvatarAdmin) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = usuario?.nombre?.take(2)?.uppercase() ?: "AD", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ExitToApp, "Salir")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorFondoApp)
                )
            }
        ) { padding ->
            // Usamos Box para superponer el Popup sobre el contenido
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(4.dp))

                    // Card Bienvenida Admin
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorCardAdmin),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(50.dp).clip(CircleShape), color = ColorAvatarAdmin) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Shield, "Admin", tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(text = "Bienvenido/a, Administrador", style = MaterialTheme.typography.bodySmall, color = Color(0xFF0D47A1))
                                Text(text = usuario?.nombre ?: "Administrador", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                            }
                        }
                    }

                    // Estadísticas de Usuarios
                    Text(text = "Estadísticas del Sistema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Total Usuarios
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.People, "Total", modifier = Modifier.size(32.dp), tint = Color(0xFF1976D2))
                                Spacer(Modifier.height(8.dp))
                                Text(totalUsuarios.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                                Text("Total Usuarios", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1976D2))
                            }
                        }

                        // Usuarios Activos
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CheckCircle, "Activos", modifier = Modifier.size(32.dp), tint = Color(0xFF4CAF50))
                                Spacer(Modifier.height(8.dp))
                                Text(usuariosActivos.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                Text("Activos", style = MaterialTheme.typography.labelMedium, color = Color(0xFF4CAF50))
                            }
                        }

                        // Usuarios Inactivos
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Cancel, "Inactivos", modifier = Modifier.size(32.dp), tint = Color(0xFFE91E63))
                                Spacer(Modifier.height(8.dp))
                                Text(usuariosInactivos.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                                Text("Inactivos", style = MaterialTheme.typography.labelMedium, color = Color(0xFFE91E63))
                            }
                        }
                    }

                    // Accesos Rápidos Admin
                    Text(text = "Gestión Rápida", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickAccessCard(modifier = Modifier.weight(1f), icon = Icons.Default.ScheduleSend, title = "Automatización", onClick = onNavigateToMensajes, color = Color(0xFFE3F2FD))
                        QuickAccessCard(modifier = Modifier.weight(1f), icon = Icons.Default.Security, title = "Roles", onClick = onNavigateToRoles, color = Color(0xFFE8F5E9))
                        QuickAccessCard(modifier = Modifier.weight(1f), icon = Icons.Default.People, title = "Empleados", onClick = onNavigateToUsuarios, color = Color(0xFFFFF3E0))
                    }

                    Spacer(Modifier.weight(1f))

                    // Card Info Contacto
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorCardAccesos),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(20.dp), tint = Color(0xFF1976D2))
                                Spacer(Modifier.width(8.dp))
                                Text(text = "Panel de Administración", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text(text = usuario?.correo ?: "admin@tcs.com", style = MaterialTheme.typography.bodySmall)
                            }
                            if (usuario?.telefono != null) {
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                    Spacer(Modifier.width(8.dp))
                                    Text(text = usuario.telefono, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // Popup animado (Ahora visible también para Admin)
                AnimatedVisibility(
                    visible = mensajePopup != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                ) {
                    mensajePopup?.let { msg ->
                        MensajePopup(
                            mensaje = msg,
                            onDismiss = { actividadViewModel.marcarMensajeVisto(msg) }
                        )
                    }
                }
            }
        }
    }
}