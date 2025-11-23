package dev.jeff.apponboarding.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.jeff.apponboarding.data.model.Actividad
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

val AzulOscuro = Color(0xFF0D1B3E)
val VerdeExito = Color(0xFF4CAF50)
val FondoGris = Color(0xFFF5F5F5)

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
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
        item {
            ProgressCard(actividades = actividades, progreso = progreso)
        }

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

        if (error != null) {
            item {
                Text(text = error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }
        }

        // --- CORRECCIÓN EN LAZYCOLUMN ---
        items(actividades.filter { !it.isCompleted() }, key = { it.id ?: it.hashCode() }) { actividad ->
            ProxPasoCard(
                actividad = actividad,
                onCheckChange = { viewModel.toggleTask(actividad) }
            )
        }
    }
}

@Composable
private fun ProgressCard(actividades: List<Actividad>, progreso: Float) {
    // ... (El código de esta tarjeta no necesita cambios)
}

// --- CORRECCIÓN EN PROXPASOCARD Y CHECKBOX ---
@Composable
private fun ProxPasoCard(actividad: Actividad, onCheckChange: () -> Unit) {
    val fechaFormatter = DateTimeFormatter.ofPattern("EEE, d MMM")
    val horaFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val (fecha, hora) = try {
        val odt = OffsetDateTime.parse(actividad.fechaInicio ?: "")
        odt.format(fechaFormatter) to odt.format(horaFormatter)
    } catch (e: Exception) {
        "" to ""
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
                    text = actividad.titulo ?: "",
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