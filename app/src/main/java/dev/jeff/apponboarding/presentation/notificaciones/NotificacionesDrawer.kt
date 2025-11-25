package dev.jeff.apponboarding.presentation.notificaciones


import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.ActividadModel
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesDrawer(
    actividades: List<ActividadModel>,
    isLoading: Boolean,
    onActividadClick: (ActividadModel) -> Unit,
    onMarcarCompletada: (ActividadModel) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(380.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
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
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Notificaciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${actividades.size} pendientes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        }

        Divider()

        // Contenido
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                }

                actividades.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "¡Todo listo!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "No tienes actividades pendientes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = actividades,
                            key = { it.id ?: UUID.randomUUID().toString() }
                        ) { actividad ->
                            NotificacionItem(
                                actividad = actividad,
                                onClick = { onActividadClick(actividad) },
                                onMarcarCompletada = { onMarcarCompletada(actividad) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificacionItem(
    actividad: ActividadModel,
    onClick: () -> Unit,
    onMarcarCompletada: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono indicador
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = getColorByTipo(actividad.tipo)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        getIconByTipo(actividad.tipo),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Contenido
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = actividad.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip de estado
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = actividad.estado,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    // Fecha
                    Text(
                        text = formatFechaNotificacion(actividad.fechaInicio),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botón de acción
            Box {
                IconButton(
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50)
                                )
                                Text("Marcar como completada")
                            }
                        },
                        onClick = {
                            onMarcarCompletada()
                            showMenu = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null
                                )
                                Text("Ver detalles")
                            }
                        },
                        onClick = {
                            onClick()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

// Funciones auxiliares
private fun getIconByTipo(tipo: String) = when (tipo.lowercase()) {
    "tarea" -> Icons.Default.Assignment
    "reunion" -> Icons.Default.Event
    "documento" -> Icons.Default.Description
    "capacitacion" -> Icons.Default.School
    else -> Icons.Default.Circle
}

private fun getColorByTipo(tipo: String) = when (tipo.lowercase()) {
    "tarea" -> Color(0xFF2196F3)
    "reunion" -> Color(0xFF9C27B0)
    "documento" -> Color(0xFFFF9800)
    "capacitacion" -> Color(0xFF4CAF50)
    else -> Color(0xFF607D8B)
}

private fun formatFechaNotificacion(isoDate: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(isoDate)
        val ahora = ZonedDateTime.now()

        val diferencia = java.time.Duration.between(zonedDateTime, ahora)

        when {
            diferencia.toDays() == 0L -> {
                val horas = diferencia.toHours()
                if (horas == 0L) {
                    val minutos = diferencia.toMinutes()
                    "Hace ${minutos}m"
                } else {
                    "Hace ${horas}h"
                }
            }
            diferencia.toDays() == 1L -> "Ayer"
            diferencia.toDays() < 7 -> "Hace ${diferencia.toDays()} días"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                zonedDateTime.format(formatter)
            }
        }
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}