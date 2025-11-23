package dev.jeff.apponboarding.presentation.actividad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.jeff.apponboarding.data.model.Actividad
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// Colores
val AzulOscuro = Color(0xFF0D1B3E)
val VerdeExito = Color(0xFF4CAF50)
val FondoGris = Color(0xFFF5F5F5)

@Composable
fun ActividadesListScreen(
    viewModel: ActividadViewModel = viewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.cargarActividades()
    }

    val actividades by viewModel.actividades.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val error by viewModel.error.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGris)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tarjeta de Progreso (Header Azul)
        item {
            ProgressCard(progreso = progreso)
        }

        // 2. Título de sección
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Próximos pasos", tint = AzulOscuro)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Próximos Pasos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )
            }
        }

        // 3. Mensaje de error (si hay)
        if (error != null) {
            item {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // 4. Lista de Tareas (CORREGIDO EL KEY)
        items(
            items = actividades,
            // 👇 SOLUCIÓN AL ERROR ROJO:
            // Si el ID es nulo, usamos hashCode() como respaldo seguro.
            key = { actividad -> actividad.id ?: actividad.hashCode() }
        ) { actividad ->
            ProxPasoCard(
                actividad = actividad,
                onCheckChange = { viewModel.toggleTask(actividad) }
            )
        }
    }
}

// --- COMPONENTES UI ---

@Composable
private fun ProgressCard(progreso: Float) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AzulOscuro),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calendario de\nActividades",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.size(60.dp),
                    color = VerdeExito,
                    trackColor = Color.Gray,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(progreso * 100).toInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Listo",
                        color = Color.LightGray,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProxPasoCard(actividad: Actividad, onCheckChange: () -> Unit) {
    val fechaFormatter = DateTimeFormatter.ofPattern("EEE, d MMM")
    val horaFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Manejo seguro de fechas por si vienen vacías o mal formato
    val (fecha, hora) = try {
        if (!actividad.fechaInicio.isNullOrEmpty()) {
            val odt = OffsetDateTime.parse(actividad.fechaInicio)
            odt.format(fechaFormatter) to odt.format(horaFormatter)
        } else {
            "S/F" to "--:--"
        }
    } catch (e: Exception) {
        "Fecha" to "Hora"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = actividad.estado.equals("Completada", ignoreCase = true),
                onCheckedChange = { onCheckChange() },
                colors = CheckboxDefaults.colors(checkedColor = VerdeExito, uncheckedColor = AzulOscuro)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = actividad.titulo ?: "Sin título",
                    fontWeight = FontWeight.SemiBold,
                    color = AzulOscuro,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = fecha, color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Schedule, contentDescription = "Hora", modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = hora, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = actividad.descripcion ?: "",
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}