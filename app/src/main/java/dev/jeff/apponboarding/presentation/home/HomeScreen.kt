package dev.jeff.apponboarding.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.presentation.actividad.ActividadViewModel
import dev.jeff.apponboarding.presentation.actividad.ActividadesState
import dev.jeff.apponboarding.presentation.notificaciones.NotificacionesDrawer
import kotlinx.coroutines.launch

private val ColorFondoApp = Color(0xFFFFFFFF)
private val ColorCardBienvenida = Color(0xFFEBE8F2)
private val ColorCardNotificacion = Color(0xFFFFDAD6)
private val ColorTextoNotificacion = Color(0xFF410002)
private val ColorCardProgreso = Color(0xFFF3E5F5)
private val ColorBarraProgresoFondo = Color(0xFFFFFFFF)
private val ColorBarraProgresoRelleno = Color(0xFF6750A4)
private val ColorCardAccesos = Color(0xFFEBE8F2)
private val ColorIconoAccesos = Color(0xFF5E5375)
private val ColorAvatar = Color(0xFF6750A4)

data class DrawerMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    usuario: UsuarioModel?,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    actividadViewModel: ActividadViewModel,
    onNavigateToActividades: () -> Unit,
    onNavigateToRecursos: () -> Unit,
    onNavigateToRoles: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    onNavigateToSupervisor: () -> Unit,
    onNavigateToAyuda: () -> Unit,
    onNavigateToMensajes: () -> Unit,
    onNavigateToUsuarios: () -> Unit,
    onNavigateToActividadDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val notificacionesDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("inicio") }
    
    // Para pruebas: asumimos que todos son admin por ahora
    val isAdmin = true // usuario?.rolRef != null 

    // Items del menú
    val menuItems = mutableListOf(
    val pendientesCount by actividadViewModel.pendientesCount.collectAsState()
    val actividadesState by actividadViewModel.actividadesState.collectAsState()
    val notificacionesList by actividadViewModel.notificacionesState.collectAsState()
    val mensajePopup by actividadViewModel.mensajeEmergente.collectAsState()

    LaunchedEffect(usuario) {
        usuario?.let {
            actividadViewModel.loadActividadesByUsuario(it.id?.toString() ?: "")
        }
    }

    val listaTareasReales = when (val state = actividadesState) {
        is ActividadesState.Success -> state.actividades
        else -> emptyList()
    }
    val totalTareas = listaTareasReales.size
    val tareasCompletadas = listaTareasReales.count { it.estado.equals("completada", ignoreCase = true) }
    val progresoFloat = if (totalTareas > 0) tareasCompletadas.toFloat() / totalTareas else 0f
    val progresoPorcentajeInt = (progresoFloat * 100).toInt()
    val (mensajeMotivador, etapaDinamica) = when {
        totalTareas == 0 -> Pair("Estamos preparando tu plan.", "Sin Asignaciones")
        progresoFloat == 0f -> Pair("Tu aventura comienza ahora.", "Inicio")
        progresoFloat < 0.5f -> Pair("Buen comienzo, sigue así.", "En Curso")
        progresoFloat < 1f -> Pair("Estás muy cerca, ¡continúa!", "Avanzado")
        else -> Pair("¡Todo listo! Excelente trabajo.", "Finalizado")
    }
    val textoEtapa = if (!usuario?.nivelOnboarding?.etapa.isNullOrBlank() && usuario?.nivelOnboarding?.etapa != "N/A") {
        usuario!!.nivelOnboarding!!.etapa
    } else {
        etapaDinamica
    }

    val menuItems = listOf(
        DrawerMenuItem("inicio", "Inicio", Icons.Outlined.Home, Icons.Filled.Home),
        DrawerMenuItem("chat", "Asistente Virtual", Icons.Outlined.Chat, Icons.Filled.Chat),
        DrawerMenuItem("mensajes", "Automatización", Icons.Outlined.ScheduleSend, Icons.Filled.ScheduleSend),
        DrawerMenuItem("supervisor", "Mi Supervisor", Icons.Outlined.SupervisorAccount, Icons.Filled.SupervisorAccount),
        DrawerMenuItem("actividades", "Mis Actividades", Icons.Outlined.Assignment, Icons.Filled.Assignment),
        DrawerMenuItem("recursos", "Recursos", Icons.Outlined.Folder, Icons.Filled.Folder),
        DrawerMenuItem("roles", "Gestionar Roles", Icons.Outlined.Security, Icons.Filled.Security),
        DrawerMenuItem("usuarios", "Gestionar Empleados", Icons.Outlined.People, Icons.Filled.People),
        DrawerMenuItem("perfil", "Mi Información", Icons.Outlined.Person, Icons.Filled.Person),
        // Se elimina "Ayuda"
        DrawerMenuItem("configuracion", "Configuración", Icons.Outlined.Settings, Icons.Filled.Settings)
    )

    if (isAdmin) {
        // Agregar items de admin
        menuItems.add(4, DrawerMenuItem("roles", "Gestionar Roles", Icons.Outlined.Security, Icons.Filled.Security))
        menuItems.add(5, DrawerMenuItem("history", "Historial Chat", Icons.Outlined.History, Icons.Filled.History))
    }

    ModalNavigationDrawer(
        drawerState = notificacionesDrawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.primaryContainer // Adaptable al tema
            ) {
                // Header del Drawer
                DrawerHeader(
                    usuario = usuario,
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )

                Spacer(Modifier.height(8.dp))

                // Items del menú
                menuItems.forEach { item ->
                    DrawerItem(
                        item = item,
                        isSelected = selectedItem == item.id,
                        onClick = {
                            selectedItem = item.id
                            scope.launch { drawerState.close() }

                            when (item.id) {
                                "chat" -> onNavigateToChat()
                                "actividades" -> onNavigateToActividades()
                                "recursos" -> onNavigateToRecursos()
                                "roles" -> onNavigateToRoles()
                                "history" -> onNavigateToHistory()
                                "configuracion" -> onNavigateToConfiguracion()
            NotificacionesDrawer(
                actividades = notificacionesList,
                isLoading = actividadesState is ActividadesState.Loading,
                onActividadClick = { actividad ->
                    scope.launch {
                        notificacionesDrawerState.close()
                        onNavigateToActividadDetail(actividad.id ?: "")
                    }
                },
                onMarcarCompletada = { actividad ->
                    usuario?.let {
                        actividadViewModel.cambiarEstadoActividad(
                            actividadId = actividad.id ?: "",
                            actividad = actividad,
                            nuevoEstado = "completada",
                            usuarioRef = it.id?.toString() ?: ""
                        )
                    }
                },
                onClose = { scope.launch { notificacionesDrawerState.close() } }
            )
        },
        gesturesEnabled = notificacionesDrawerState.isOpen
    ) {
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
                    menuItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.title, color = Color.White) },
                            icon = { Icon(if (selectedItem == item.id) item.selectedIcon else item.icon, null, tint = Color.White) },
                            selected = selectedItem == item.id,
                            onClick = {
                                selectedItem = item.id
                                scope.launch { drawerState.close() }
                                when (item.id) {
                                    "chat" -> onNavigateToChat()
                                    "mensajes" -> onNavigateToMensajes()
                                    "supervisor" -> onNavigateToSupervisor()
                                    "actividades" -> onNavigateToActividades()
                                    "recursos" -> onNavigateToRecursos()
                                    "roles" -> onNavigateToRoles()
                                    "usuarios" -> onNavigateToUsuarios()
                                    "ayuda" -> onNavigateToAyuda()
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
                                        Icon(Icons.Default.Business, null, modifier = Modifier.size(20.dp), tint = Color(0xFF1D1B20))
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("TCS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Menú")
                            }
                        },
                        actions = {
                            Surface(modifier = Modifier.size(32.dp).clip(CircleShape), color = ColorAvatar) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = usuario?.nombre?.take(2)?.uppercase() ?: "US", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(onClick = {
                                usuario?.let { actividadViewModel.loadActividadesByUsuario(it.id?.toString() ?: "") }
                                scope.launch { notificacionesDrawerState.open() }
                            }) {
                                BadgedBox(badge = {
                                    if (pendientesCount > 0) Badge(containerColor = Color.Red, contentColor = Color.White) { Text(pendientesCount.toString()) }
                                }) {
                                    Icon(Icons.Default.Notifications, "Notificaciones")
                                }
                            }
                        }

                        // Cambiar tema claro/oscuro
                        IconButton(
                            onClick = onToggleTheme
                        ) {
                            Icon(
                                if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Cambiar tema"
                            )
                        }

                        // Cerrar sesión
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                            IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                                Icon(Icons.Default.DarkMode, "Tema")
                            }
                            IconButton(onClick = onLogout) {
                                Icon(Icons.Default.ExitToApp, "Salir")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorFondoApp)
                    )
                }
            ) { padding ->
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

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ColorCardBienvenida),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(50.dp).clip(CircleShape), color = ColorAvatar) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = usuario?.nombre?.take(2)?.uppercase() ?: "JE", style = MaterialTheme.typography.titleLarge, color = Color.White)
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(text = "Bienvenido/a", style = MaterialTheme.typography.bodySmall, color = Color.Black)
                                    Text(text = usuario?.nombre ?: "Usuario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }

                        if (pendientesCount > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    scope.launch { notificacionesDrawerState.open() }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = ColorCardNotificacion),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Notifications, null, tint = ColorTextoNotificacion)
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(text = "Tienes $pendientesCount notificaciones", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ColorTextoNotificacion)
                                            Text(text = "Toca para ver detalles", style = MaterialTheme.typography.bodySmall, color = ColorTextoNotificacion.copy(alpha = 0.8f))
                                        }
                                    }
                                    Icon(Icons.Default.ArrowForward, null, tint = ColorTextoNotificacion)
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToActividades() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ColorCardProgreso),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Progreso de Onboarding", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1D0061))
                                    Text(text = "$progresoPorcentajeInt%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1D0061))
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(text = "Etapa: $textoEtapa", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1D0061))
                                Spacer(Modifier.height(12.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(50)).background(ColorBarraProgresoFondo)) {
                                    Box(modifier = Modifier.fillMaxWidth(progresoFloat).fillMaxHeight().background(ColorBarraProgresoRelleno))
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(text = mensajeMotivador, style = MaterialTheme.typography.labelSmall, color = Color(0xFF1D0061).copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
                            }
                        }

                        Text(text = "Accesos Rápidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            QuickAccessCard(modifier = Modifier.weight(1f), icon = Icons.Default.ChatBubble, title = "Asistente", onClick = onNavigateToChat)
                            QuickAccessCard(modifier = Modifier.weight(1f), icon = Icons.Default.Assignment, title = "Actividades", onClick = onNavigateToActividades)
                            QuickAccessCard(modifier = Modifier.weight(1f), icon = Icons.Default.Folder, title = "Recursos", onClick = onNavigateToRecursos)
                        }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAccessCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Chat,
                        title = "Asistente",
                        onClick = onNavigateToChat
                    )
                    QuickAccessCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Assignment,
                        title = "Actividades",
                        onClick = onNavigateToActividades
                    )
                    QuickAccessCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Folder,
                        title = "Recursos",
                        onClick = onNavigateToRecursos
                    )
                }
                
                if (isAdmin) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.History,
                            title = "Historial Chat",
                            onClick = onNavigateToHistory
                        )
                        // Espacio para más botones admin
                        Spacer(Modifier.weight(2f))
                    }
                }
                        Spacer(Modifier.weight(1f))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ColorCardAccesos),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Información de contacto", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                    Spacer(Modifier.width(8.dp))
                                    Text(text = usuario?.correo ?: "jeff@gmail.com", style = MaterialTheme.typography.bodySmall)
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
}

@Composable
fun DrawerHeader(
    usuario: UsuarioModel?,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCloseDrawer) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar menú",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer // Adaptable
                )
            }
            Text(
                text = "Cerrar menú",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun DrawerItem(
    item: DrawerMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    isLogout: Boolean = false
) {
    // Colores adaptables al tema
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val contentColor = when {
        isLogout -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = item.title,
                tint = contentColor
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
fun QuickAccessCard(modifier: Modifier = Modifier, icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = modifier.aspectRatio(1f).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardAccesos),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(32.dp), tint = ColorIconoAccesos)
            Spacer(Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Black)
        }
    }
}