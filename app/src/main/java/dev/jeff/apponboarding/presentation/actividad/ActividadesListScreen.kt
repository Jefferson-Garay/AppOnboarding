package dev.jeff.apponboarding.presentation.actividad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeff.apponboarding.data.model.ActividadModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// Colores locales si no importas el Theme
private val AzulOscuro = Color(0xFF0D1B3E)
private val VerdeExito = Color(0xFF4CAF50)
private val FondoGris = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesListScreen(
    viewModel: ActividadViewModel,
    usuarioRef: String,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.actividadesState.collectAsState()
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    LaunchedEffect(Unit) {
        viewModel.loadActividadesByUsuario(usuarioRef)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Actividades") }) },
        floatingActionButton = { FloatingActionButton(onClick = onNavigateToCreate) { Icon(Icons.Default.Add, "Agregar") } }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FondoGris)
        ) {
            when (val currentState = state) {
                is ActividadesState.Success -> {
                    // --- FIX: FILTRAR MENSAJES OCULTOS ---
                    // Ignoramos cualquier actividad cuyo tipo empiece con "MSG_"
                    val actividadesReales = currentState.actividades.filter { !it.tipo.startsWith("MSG_") }

                    ProgressHeader(actividades = actividadesReales)

                    // Chips de Filtro
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filtroSeleccionado == "Todas",
                            onClick = { filtroSeleccionado = "Todas" },
                            label = { Text("Todas") }
                        )
                        FilterChip(
                            selected = filtroSeleccionado == "Pendientes",
                            onClick = { filtroSeleccionado = "Pendientes" },
                            label = { Text("Pendientes") }
                        )
                        FilterChip(
                            selected = filtroSeleccionado == "Completadas",
                            onClick = { filtroSeleccionado = "Completadas" },
                            label = { Text("Listas") }
                        )
                    }

                    val actividadesFiltradas = when(filtroSeleccionado) {
                        "Pendientes" -> actividadesReales.filter { !it.estado.equals("Completada", ignoreCase = true) }
                        "Completadas" -> actividadesReales.filter { it.estado.equals("Completada", ignoreCase = true) }
                        else -> actividadesReales
                    }

                    if (actividadesFiltradas.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("No hay actividades en esta categoría", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(actividadesFiltradas, key = { it.id ?: it.hashCode() }) { actividad ->
                                ActividadCard(
                                    actividad = actividad,
                                    onToggle = {
                                        val nuevoEstado = if (actividad.estado.equals("Completada", ignoreCase = true)) "Pendiente" else "Completada"
                                        viewModel.cambiarEstadoActividad(actividad.id ?: "", actividad, nuevoEstado, usuarioRef)
                                    },
                                    onClick = { onNavigateToDetail(actividad.id ?: "") }
                                )
                            }
                        }
                    }
                }
                is ActividadesState.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                }
                is ActividadesState.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Error: ${currentState.message}") }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ProgressHeader(actividades: List<ActividadModel>) {
    val completadas = actividades.count { it.estado.equals("Completada", ignoreCase = true) }
    val total = actividades.size
    val progreso = if (total > 0) completadas.toFloat() / total.toFloat() else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = AzulOscuro),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tu Progreso",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = "${(progreso * 100).toInt()}% Completado",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completadas de $total actividades",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progreso },
                    color = VerdeExito,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeWidth = 6.dp,
                    modifier = Modifier.size(60.dp)
                )
                if (progreso == 1f) {
                    Icon(Icons.Default.CheckCircle, null, tint = VerdeExito, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ActividadCard(actividad: ActividadModel, onToggle: () -> Unit, onClick: () -> Unit) {
    val isCompleted = actividad.estado.equals("Completada", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if(isCompleted) Color(0xFFE8F5E9) else Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = VerdeExito,
                    uncheckedColor = AzulOscuro,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if(isCompleted) Color.Gray else Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(text = actividad.tipo, icon = Icons.Default.CalendarToday)

                    val formattedDate = try {
                        if (actividad.fechaInicio.contains("T")) {
                            OffsetDateTime.parse(actividad.fechaInicio).format(DateTimeFormatter.ofPattern("dd MMM"))
                        } else {
                            actividad.fechaInicio
                        }
                    } catch (e: Exception) {
                        "S/F"
                    }
                    InfoChip(text = formattedDate, icon = Icons.Default.Schedule)
                }
            }
        }
    }
}

@Composable
fun InfoChip(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}