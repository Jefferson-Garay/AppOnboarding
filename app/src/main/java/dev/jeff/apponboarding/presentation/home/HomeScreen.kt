package dev.jeff.apponboarding.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import dev.jeff.apponboarding.data.model.UsuarioModel
import kotlinx.coroutines.launch

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
    onNavigateToActividades: () -> Unit,
    onNavigateToRecursos: () -> Unit,
    onNavigateToRoles: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("inicio") }
    
    // Para pruebas: asumimos que todos son admin por ahora
    val isAdmin = true // usuario?.rolRef != null 

    // Items del menú
    val menuItems = mutableListOf(
        DrawerMenuItem("inicio", "Inicio", Icons.Outlined.Home, Icons.Filled.Home),
        DrawerMenuItem("chat", "Asistente Virtual", Icons.Outlined.Chat, Icons.Filled.Chat),
        DrawerMenuItem("actividades", "Mis Actividades", Icons.Outlined.Assignment, Icons.Filled.Assignment),
        DrawerMenuItem("recursos", "Recursos", Icons.Outlined.Folder, Icons.Filled.Folder),
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
        drawerState = drawerState,
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
                            }
                        }
                    )
                }

                Spacer(Modifier.weight(1f))

                // Botón de cerrar sesión al final
                DrawerItem(
                    item = DrawerMenuItem("logout", "Cerrar Sesión", Icons.Outlined.ExitToApp),
                    isSelected = false,
                    onClick = onLogout,
                    isLogout = true
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Logo placeholder
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Business,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = "TCS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        // Avatar del usuario
                        Surface(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = usuario?.nombre?.take(2)?.uppercase() ?: "US",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        // Notificaciones
                        IconButton(onClick = { /* TODO: Notificaciones */ }) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text("7")
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
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
                    )
                )
            }
        ) { padding ->
            // Contenido principal del Home
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card de bienvenida
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = usuario?.nombre?.take(2)?.uppercase() ?: "US",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Bienvenido/a",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = usuario?.nombre ?: "Usuario",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (usuario?.area != null) {
                                Text(
                                    text = usuario.area,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Card de progreso de Onboarding
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso de Onboarding",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${usuario?.nivelOnboarding?.porcentaje ?: 0}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Etapa: ${usuario?.nivelOnboarding?.etapa ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = (usuario?.nivelOnboarding?.porcentaje ?: 0) / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }

                // Accesos rápidos
                Text(
                    text = "Accesos Rápidos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

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

                // Información de contacto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información de contacto",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = usuario?.correo ?: "N/A",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (usuario?.telefono != null) {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = usuario.telefono,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
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
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}