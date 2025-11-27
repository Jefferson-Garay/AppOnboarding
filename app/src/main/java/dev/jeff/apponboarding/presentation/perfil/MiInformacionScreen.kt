package dev.jeff.apponboarding.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.ActividadRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiInformacionScreen(
    usuario: UsuarioModel?,
    onNavigateBack: () -> Unit,
    onNavigateToActividades: () -> Unit,
    onNavigateToSupervisor: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val actividadRepository = remember { ActividadRepository() }
    val usuarioRepository = remember { UsuarioRepository() }

    var actividades by remember { mutableStateOf<List<ActividadModel>>(emptyList()) }
    var supervisor by remember { mutableStateOf<UsuarioModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar datos del usuario
    LaunchedEffect(usuario) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Cargar actividades del usuario
                usuario?.id?.let { userId ->
                    actividades = actividadRepository.getActividadesByUsuario(userId.toString())
                }

                // Buscar supervisor (administrador)
                val usuarios = usuarioRepository.getUsuarios()
                supervisor = usuarios.firstOrNull { user ->
                    user.rolRef == "6913adbcca79acfd93858d5c" ||
                            user.rolRef?.contains("admin", ignoreCase = true) == true
                }
            } catch (e: Exception) {
                errorMessage = "Error al cargar datos"
            } finally {
                isLoading = false
            }
        }
    }

    // Estadísticas
    val totalActividades = actividades.size
    val actividadesCompletadas = actividades.count { it.estado.equals("completada", ignoreCase = true) }
    val actividadesPendientes = actividades.count { it.estado.equals("pendiente", ignoreCase = true) }
    val actividadesEnProceso = actividades.count { it.estado.equals("en proceso", ignoreCase = true) }
    val porcentajeCompletado = if (totalActividades > 0)
        (actividadesCompletadas.toFloat() / totalActividades * 100).toInt()
    else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Mi Información")
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header con avatar y nombre
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Avatar grande
                            Surface(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 8.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = usuario?.nombre?.take(2)?.uppercase() ?: "US",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Nombre
                            Text(
                                text = usuario?.nombre ?: "Usuario",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            // Correo
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = usuario?.correo ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Estado
                            Surface(
                                color = if (usuario?.estado?.equals("Activo", ignoreCase = true) == true)
                                    Color(0xFF4CAF50)
                                else
                                    MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Circle,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = Color.White
                                    )
                                    Text(
                                        text = usuario?.estado ?: "Estado desconocido",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Sección: Información Personal
                item {
                    Text(
                        text = "Información Personal",
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
                            // Área
                            InfoItem(
                                icon = Icons.Default.BusinessCenter,
                                label = "Área",
                                value = usuario?.area ?: "No especificada"
                            )

                            Divider()

                            // Teléfono
                            InfoItem(
                                icon = Icons.Default.Phone,
                                label = "Teléfono",
                                value = usuario?.telefono ?: "No especificado"
                            )

                            Divider()

                            // Estado
                            InfoItem(
                                icon = Icons.Default.Circle,
                                label = "Estado",
                                value = usuario?.estado ?: "Activo"
                            )
                        }
                    }
                }

                // Sección: Mi Supervisor
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Mi Supervisor",
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
                        onClick = onNavigateToSupervisor
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (supervisor != null) {
                                        Text(
                                            text = supervisor!!.nombre.take(2).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.SupervisorAccount,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = supervisor?.nombre ?: "No asignado",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = supervisor?.correo ?: "Sin información",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Ver detalles",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Sección: Progreso de Onboarding
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Progreso de Onboarding",
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
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Porcentaje
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Completado",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$porcentajeCompletado%",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Barra de progreso
                            LinearProgressIndicator(
                                progress = porcentajeCompletado / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            // Etapa
                            usuario?.nivelOnboarding?.let { nivel ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = "Etapa: ${nivel.etapa}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Sección: Mis Actividades (Estadísticas)
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Mis Actividades",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total
                        EstadisticaCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Assignment,
                            label = "Total",
                            value = totalActividades.toString(),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Completadas
                        EstadisticaCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CheckCircle,
                            label = "Completadas",
                            value = actividadesCompletadas.toString(),
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Pendientes
                        EstadisticaCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Schedule,
                            label = "Pendientes",
                            value = actividadesPendientes.toString(),
                            color = Color(0xFFFF9800)
                        )

                        // En Proceso
                        EstadisticaCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.HourglassEmpty,
                            label = "En Proceso",
                            value = actividadesEnProceso.toString(),
                            color = Color(0xFF2196F3)
                        )
                    }
                }

                // Botón para ver todas las actividades
                item {
                    Button(
                        onClick = onNavigateToActividades,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Ver todas mis actividades")
                    }
                }

                // Últimas actividades
                if (actividades.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Últimas Actividades",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(actividades.take(3)) { actividad ->
                        ActividadCompactCard(actividad = actividad)
                    }
                }

                // Espaciado final
                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String
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
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EstadisticaCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActividadCompactCard(
    actividad: ActividadModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono según tipo
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = when (actividad.tipo?.lowercase()) {
                    "tarea" -> Color(0xFF2196F3).copy(alpha = 0.1f)
                    "reunion" -> Color(0xFF9C27B0).copy(alpha = 0.1f)
                    "documento" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                    "capacitacion" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (actividad.tipo?.lowercase()) {
                            "tarea" -> Icons.Default.Assignment
                            "reunion" -> Icons.Default.Event
                            "documento" -> Icons.Default.Description
                            "capacitacion" -> Icons.Default.School
                            else -> Icons.Default.Circle
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = when (actividad.tipo?.lowercase()) {
                            "tarea" -> Color(0xFF2196F3)
                            "reunion" -> Color(0xFF9C27B0)
                            "documento" -> Color(0xFFFF9800)
                            "capacitacion" -> Color(0xFF4CAF50)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = actividad.descripcion ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Chip de estado
            Surface(
                color = when (actividad.estado?.lowercase()) {
                    "completada" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    "en proceso" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                    "pendiente" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = actividad.estado ?: "Sin estado",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (actividad.estado?.lowercase()) {
                        "completada" -> Color(0xFF4CAF50)
                        "en proceso" -> Color(0xFF2196F3)
                        "pendiente" -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}