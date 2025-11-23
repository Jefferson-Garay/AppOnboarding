package dev.jeff.apponboarding.presentation.recurso

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.repository.RecursoRepository
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursoDetailScreen(
    recursoId: String,
    onNavigateBack: () -> Unit
) {
    val repository = remember { RecursoRepository() }
    var recurso by remember { mutableStateOf<dev.jeff.apponboarding.data.model.RecursoModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEstadoDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(recursoId) {
        scope.launch {
            isLoading = true
            recurso = repository.getRecursoById(recursoId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Recurso") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            recurso?.let {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.link))
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Abrir enlace")
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

                recurso != null -> {
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
                                    text = recurso!!.descripcion,
                                    style = MaterialTheme.typography.headlineSmall
                                )

                                Divider()

                                RecursoDetailRow(label = "Tipo", value = recurso!!.tipo)

                                RecursoDetailRow(label = "Estado", value = recurso!!.estado)

                                RecursoDetailRow(
                                    label = "Enlace",
                                    value = recurso!!.link,
                                    isLink = true
                                )

                                RecursoDetailRow(
                                    label = "Fecha de subida",
                                    value = formatRecursoDate(recurso!!.fechaSubida)
                                )
                            }
                        }

                        // Botón para cambiar estado
                        Button(
                            onClick = { showEstadoDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cambiar Estado")
                        }

                        // Abrir enlace en navegador
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recurso!!.link))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Abrir Recurso")
                        }
                    }
                }

                else -> {
                    Text(
                        text = "No se pudo cargar el recurso",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            }
        }
    }

    // Dialog para cambiar estado
    if (showEstadoDialog) {
        var selectedEstado by remember { mutableStateOf(recurso?.estado ?: "Activo") }
        val estados = listOf("Activo", "Inactivo", "Archivado")

        AlertDialog(
            onDismissRequest = { showEstadoDialog = false },
            title = { Text("Cambiar Estado") },
            text = {
                Column {
                    estados.forEach { estado ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedEstado == estado,
                                onClick = { selectedEstado = estado }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(estado)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.updateEstadoRecurso(recursoId, selectedEstado)
                            recurso = repository.getRecursoById(recursoId)
                        }
                        showEstadoDialog = false
                    }
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEstadoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RecursoDetailRow(label: String, value: String, isLink: Boolean = false) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isLink) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

fun formatRecursoDate(isoDate: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        isoDate
    }
}