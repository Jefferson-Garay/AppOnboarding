package dev.jeff.apponboarding.presentation.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.jeff.apponboarding.data.model.Actividad
import dev.jeff.apponboarding.presentation.home.AzulOscuro
import dev.jeff.apponboarding.presentation.home.FondoGris
import dev.jeff.apponboarding.presentation.home.HomeViewModel
import dev.jeff.apponboarding.presentation.home.VerdeExito
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CalendarioScreen(viewModel: HomeViewModel = viewModel()) {
    // Solución Definitiva (Parte 2): Cargar datos solo cuando la pantalla es visible.
    LaunchedEffect(Unit) {
        viewModel.cargarActividades()
    }

    val actividades by viewModel.actividades.collectAsState()
    val error by viewModel.error.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGris),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CalendarioHeader(actividades = actividades)
        }

        if (error != null) {
            item {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        items(actividades) { actividad ->
            CalendarioTaskCard(actividad = actividad, onCheckChange = {
                viewModel.toggleTask(actividad)
            })
        }

        item {
            Text(
                text = "© 2025 Tata Consultancy Services. Todos los derechos reservados.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarioHeader(actividades: List<Actividad>) {
    val cantCompletadas = actividades.count { it.isCompleted() }
    val totalActividades = actividades.size

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulOscuro),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calendario de Actividades",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$cantCompletadas de $totalActividades completadas",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CalendarioTaskCard(actividad: Actividad, onCheckChange: () -> Unit) {
    val cardColor = if (actividad.isCompleted()) Color(0xFFE8F5E9) else Color.White
    val checkboxColor = if (actividad.isCompleted()) VerdeExito else AzulOscuro
    
    val fechaFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy")
    val horaFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val (fecha, hora) = try {
        val odt = OffsetDateTime.parse(actividad.fechaInicio ?: "")
        odt.format(fechaFormatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } to odt.format(horaFormatter)
    } catch (e: Exception) {
        "" to ""
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = actividad.isCompleted(),
                onCheckedChange = { onCheckChange() },
                colors = CheckboxDefaults.colors(checkedColor = checkboxColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = actividad.titulo ?: "",
                    fontWeight = FontWeight.SemiBold,
                    color = AzulOscuro,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Text(text = fecha, color = Color.Gray, fontSize = 12.sp)
                     Spacer(modifier = Modifier.weight(1f))
                     Text(text = hora, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = actividad.descripcion ?: "",
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}