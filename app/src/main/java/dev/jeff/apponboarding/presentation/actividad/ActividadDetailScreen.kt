package dev.jeff.apponboarding.presentation.actividad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.ui.theme.AzulOscuro
import dev.jeff.apponboarding.ui.theme.VerdeExito
import kotlinx.coroutines.launch
import dev.jeff.apponboarding.data.repository.ActividadRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadDetailScreen(
    actividadId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val repository = remember { ActividadRepository() }
    val scope = rememberCoroutineScope()

    var actividad by remember { mutableStateOf<ActividadModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(actividadId) {
        scope.launch {
            isLoading = true
            actividad = repository.getActividadById(actividadId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(actividadId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "editar")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF3F3F3))
        ) {

            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )

                actividad != null -> ActividadDetailContent(actividad!!)

                else -> Text(
                    text = "No se pudo cargar la actividad",
                    modifier = Modifier.fillMaxSize().wrapContentSize()
                )
            }
        }
    }
}

@Composable
fun ActividadDetailContent(actividad: ActividadModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---- CARD DE ESTADO ----
        HeaderEstadoCard(actividad.estado)

        // ---- CARD PRINCIPAL ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.headlineSmall
                )

                Divider()

                DetailItem("Descripción", actividad.descripcion)
                DetailItem("Tipo", actividad.tipo)
                DetailItem("Estado", actividad.estado)
                DetailItem("Fecha inicio", formatDate(actividad.fechaInicio))
                DetailItem("Fecha fin", formatDate(actividad.fechaFin))
            }
        }
    }
}

@Composable
fun HeaderEstadoCard(estado: String) {

    val isCompleted = estado.equals("completada", ignoreCase = true)
    val color = if (isCompleted) VerdeExito else AzulOscuro
    val label = if (isCompleted) "Completada" else "Pendiente"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text("Estado", color = Color.White.copy(alpha = 0.8f))
                Text(label, color = Color.White, style = MaterialTheme.typography.headlineSmall)
            }

            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun DetailItem(title: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, color = AzulOscuro, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

fun formatDate(fecha: String): String =
    try {
        val zdt = ZonedDateTime.parse(fecha)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        zdt.format(formatter)
    } catch (e: Exception) {
        fecha
    }
