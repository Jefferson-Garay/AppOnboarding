package dev.jeff.apponboarding.presentation.actividad


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.repository.ActividadRepository
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.HorizontalDivider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadDetailScreen(
    actividadId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val repository = remember { ActividadRepository() }
    var actividad by remember { mutableStateOf<dev.jeff.apponboarding.data.model.ActividadModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

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
                        //Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(actividadId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }

                actividad != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = actividad!!.titulo,
                                    style = MaterialTheme.typography.headlineSmall
                                )

                                //Divider()
                                HorizontalDivider()

                                DetailRow(label = "Descripción", value = actividad!!.descripcion)

                                if (actividad!!.tipo.isNotEmpty()) {
                                    DetailRow(label = "Tipo", value = actividad!!.tipo)
                                }

                                DetailRow(label = "Estado", value = actividad!!.estado)

                                DetailRow(
                                    label = "Fecha de inicio",
                                    value = formatDate(actividad!!.fechaInicio)
                                )

                                DetailRow(
                                    label = "Fecha de fin",
                                    value = formatDate(actividad!!.fechaFin)
                                )
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text = "No se pudo cargar la actividad",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun formatDate(isoDate: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        isoDate
    }
}