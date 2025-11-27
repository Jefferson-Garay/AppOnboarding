package dev.jeff.apponboarding.presentation.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import dev.jeff.apponboarding.data.model.UsuarioModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    usuario: UsuarioModel?,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    var notificacionesActivadas by remember { mutableStateOf(true) }
    var sonidosActivados by remember { mutableStateOf(true) }
    var vibracionActivada by remember { mutableStateOf(true) }
    var notificacionesActividades by remember { mutableStateOf(true) }
    var notificacionesRecursos by remember { mutableStateOf(true) }
    var notificacionesChat by remember { mutableStateOf(true) }
    var idiomaSeleccionado by remember { mutableStateOf("Español") }
    var showIdiomaDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Configuración")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección: Información del Usuario
            item {
                Text(
                    text = "Mi Cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar y nombre
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = usuario?.nombre?.take(2)?.uppercase() ?: "US",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = usuario?.nombre ?: "Usuario",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = usuario?.correo ?: "correo@ejemplo.com",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Divider()

                        // Información del usuario (solo lectura)
                        InfoField(
                            icon = Icons.Default.Business,
                            label = "Área",
                            value = usuario?.area ?: "No especificada"
                        )

                        InfoField(
                            icon = Icons.Default.Phone,
                            label = "Teléfono",
                            value = usuario?.telefono ?: "No especificado"
                        )

                        InfoField(
                            icon = Icons.Default.Badge,
                            label = "Estado",
                            value = usuario?.estado ?: "Activo",
                            valueColor = if (usuario?.estado?.equals("Activo", ignoreCase = true) == true)
                                Color(0xFF4CAF50)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Información de onboarding
                        usuario?.nivelOnboarding?.let { nivel ->
                            Divider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(
                                            text = "Progreso de Onboarding",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Etapa: ${nivel.etapa}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Text(
                                    text = "${nivel.porcentaje}%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Sección: Apariencia
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Apariencia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tema oscuro/claro
                        SettingItemWithSwitch(
                            icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            title = "Modo Oscuro",
                            subtitle = if (isDarkTheme) "Activado" else "Desactivado",
                            checked = isDarkTheme,
                            onCheckedChange = onToggleDarkTheme
                        )
                    }
                }
            }

            // Sección: Idioma
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Idioma",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { /* showIdiomaDialog = true */ }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Idioma de la aplicación",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = idiomaSeleccionado,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Por defecto",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // Sección: Notificaciones
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Notificaciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Notificaciones generales
                        SettingItemWithSwitch(
                            icon = Icons.Default.Notifications,
                            title = "Notificaciones",
                            subtitle = if (notificacionesActivadas) "Activadas" else "Desactivadas",
                            checked = notificacionesActivadas,
                            onCheckedChange = { notificacionesActivadas = it }
                        )

                        Divider()

                        // Sonidos
                        SettingItemWithSwitch(
                            icon = Icons.Default.VolumeUp,
                            title = "Sonidos",
                            subtitle = "Reproducir sonidos de notificaciones",
                            checked = sonidosActivados,
                            onCheckedChange = { sonidosActivados = it },
                            enabled = notificacionesActivadas
                        )

                        // Vibración
                        SettingItemWithSwitch(
                            icon = Icons.Default.Vibration,
                            title = "Vibración",
                            subtitle = "Vibrar al recibir notificaciones",
                            checked = vibracionActivada,
                            onCheckedChange = { vibracionActivada = it },
                            enabled = notificacionesActivadas
                        )

                        Divider()

                        Text(
                            text = "Tipos de notificaciones",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        // Notificaciones de actividades
                        SettingItemWithSwitch(
                            icon = Icons.Default.Assignment,
                            title = "Actividades",
                            subtitle = "Nuevas tareas y recordatorios",
                            checked = notificacionesActividades,
                            onCheckedChange = { notificacionesActividades = it },
                            enabled = notificacionesActivadas
                        )

                        // Notificaciones de recursos
                        SettingItemWithSwitch(
                            icon = Icons.Default.Folder,
                            title = "Recursos",
                            subtitle = "Nuevos documentos y materiales",
                            checked = notificacionesRecursos,
                            onCheckedChange = { notificacionesRecursos = it },
                            enabled = notificacionesActivadas
                        )

                        // Notificaciones de chat
                        SettingItemWithSwitch(
                            icon = Icons.Default.Chat,
                            title = "Asistente Virtual",
                            subtitle = "Mensajes del chatbot",
                            checked = notificacionesChat,
                            onCheckedChange = { notificacionesChat = it },
                            enabled = notificacionesActivadas
                        )
                    }
                }
            }

            // Sección: Acerca de
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Acerca de",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingItem(
                            icon = Icons.Default.Info,
                            title = "Versión de la aplicación",
                            subtitle = "1.0.0"
                        )

                        Divider()

                        SettingItem(
                            icon = Icons.Default.Business,
                            title = "Empresa",
                            subtitle = "TCS - Tata Consultancy Services"
                        )

                        Divider()

                        SettingItem(
                            icon = Icons.Default.Code,
                            title = "Desarrollado por",
                            subtitle = "Equipo de Desarrollo TCS"
                        )
                    }
                }
            }

            // Espaciado final
            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoField(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.5f)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}