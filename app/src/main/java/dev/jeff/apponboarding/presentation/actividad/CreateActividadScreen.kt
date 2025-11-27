package dev.jeff.apponboarding.presentation.actividad

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.ActividadRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActividadScreen(
    viewModel: ActividadViewModel,
    usuarioRef: String,
    onNavigateBack: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("pendiente") }

    // Campo manual para ID de usuario
    var idUsuarioManual by remember { mutableStateOf("") }

    val createState by viewModel.createState.collectAsState()

    LaunchedEffect(createState) {
        if (createState is CreateActividadState.Success) {
            viewModel.resetCreateState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Actividad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Campo para usuarioRef
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Dato Obligatorio (Temporal)",
                        style = MaterialTheme.typography.labelSmall
                    )

                    OutlinedTextField(
                        value = idUsuarioManual,
                        onValueChange = { idUsuarioManual = it },
                        label = { Text("ID del Usuario (usuarioRef)") },
                        placeholder = { Text("Ej: 6742817d23...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Dropdown de estado
            var expanded by remember { mutableStateOf(false) }
            val estados = listOf("pendiente", "en proceso", "completada", "cancelada")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { seleccion ->
                        DropdownMenuItem(
                            text = { Text(seleccion) },
                            onClick = {
                                estado = seleccion
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de crear
            Button(
                onClick = {
                    val now = LocalDateTime.now()
                    val end = now.plusHours(1)
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

                    val actividad = ActividadRequest(
                        titulo = titulo,
                        descripcion = descripcion,
                        tipo = tipo.ifBlank { "General" },
                        fechaInicio = now.format(formatter),
                        fechaFin = end.format(formatter),
                        usuarioRef = idUsuarioManual.trim(),
                        estado = estado
                    )

                    Log.d("DEBUG_APP", "Creando actividad → $actividad")

                    viewModel.createActividad(actividad)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = titulo.isNotBlank() &&
                        descripcion.isNotBlank() &&
                        idUsuarioManual.isNotBlank()
            ) {
                Text("Crear Actividad")
            }

            // Estados del viewmodel
            when (createState) {
                is CreateActividadState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is CreateActividadState.Error -> {
                    Text(
                        text = (createState as CreateActividadState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> Unit
            }
        }
    }
}
