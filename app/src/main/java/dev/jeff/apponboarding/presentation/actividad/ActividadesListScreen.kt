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
import androidx.navigation.NavController
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.ui.theme.AzulOscuro
import dev.jeff.apponboarding.ui.theme.FondoGris
import dev.jeff.apponboarding.ui.theme.VerdeExito
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesListScreen(
    viewModel: ActividadViewModel,
    usuarioRef: String, 
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.actividadesState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadActividadesByUsuario(usuarioRef)
    }

    LaunchedEffect(deleteState) {
        if (deleteState is DeleteActividadState.Success) {
            viewModel.loadActividadesByUsuario(usuarioRef)
            viewModel.resetDeleteState()
        }
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
                    val actividades = currentState.actividades
                    ProgressHeader(actividades = actividades)

                    if (actividades.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("No tienes actividades aún") }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(actividades, key = { it.id ?: it.hashCode() }) { actividad ->
                                ActividadCard(
                                    actividad = actividad,
                                    // El toggle ahora se manejará en el detalle, la lista es de solo lectura por ahora
                                    onToggle = { /* No-op */ }, 
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

            if (deleteState is DeleteActividadState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
            Text(
                text = "Calendario de\nActividades",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = progreso, // Sintaxis corregida para M3
                    color = VerdeExito,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeWidth = 5.dp,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    text = "${(progreso * 100).toInt()}%\nListo",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                Text(text = actividad.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(text = actividad.tipo, icon = Icons.Default.CalendarToday)
                    // Asumo que quieres mostrar la fecha de inicio formateada
                    val formattedDate = try {
                        OffsetDateTime.parse(actividad.fechaInicio).format(DateTimeFormatter.ofPattern("dd MMM"))
                    } catch (e: Exception) {
                        "S/F"
                    }
                    InfoChip(text = formattedDate, icon = Icons.Default.Schedule)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = actividad.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
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