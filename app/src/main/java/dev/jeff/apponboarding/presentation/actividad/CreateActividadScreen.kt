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
    usuarioRef: String, // Aunque llegue vacío, usaremos el campo manual de abajo
    onNavigateBack: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("pendiente") }

    // CAMBIO 1: Variable para escribir el ID manualmente
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

            // --- CAMBIO 1 (VISUAL): CAMPO PARA EL ID DEL USUARIO ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
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
            // -----------------------------------------------------

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
                minLines = 3,
                maxLines = 5
            )

            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Selector de estado
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { selectionEstado ->
                        DropdownMenuItem(
                            text = { Text(selectionEstado) },
                            onClick = {
                                estado = selectionEstado
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // CAMBIO 2: LÓGICA DE FECHAS SEGURA
                    val now = LocalDateTime.now()
                    val end = now.plusHours(1) // La actividad dura 1 hora por defecto

                    // Formato ISO estándar (yyyy-MM-ddTHH:mm:ss)
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

                    val fechaInicioStr = now.format(formatter)
                    val fechaFinStr = end.format(formatter)

                    // Validar que el usuario puso el ID
                    val usuarioFinal = idUsuarioManual.trim()

                    // CAMBIO 3: LOG PARA VERIFICAR ANTES DE ENVIAR
                    Log.d("DEBUG_APP", "Enviando -> ID: $usuarioFinal, Inicio: $fechaInicioStr, Fin: $fechaFinStr")

                    val actividad = ActividadRequest(
                        titulo = titulo,
                        descripcion = descripcion,
                        tipo = tipo.ifBlank { "General" }, // Evitar vacíos
                        fechaInicio = fechaInicioStr,
                        fechaFin = fechaFinStr,
                        usuarioRef = usuarioFinal,
                        estado = estado
                    )
                    viewModel.createActividad(actividad)
                },
                modifier = Modifier.fillMaxWidth(),
                // El botón solo se activa si hay Título, Descripción y ID de Usuario
                enabled = titulo.isNotBlank() && descripcion.isNotBlank() && idUsuarioManual.isNotBlank()
            ) {
                Text("Crear Actividad")
            }

            when (createState) {
                is CreateActividadState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is CreateActividadState.Error -> {
                    Text(
                        text = (createState as CreateActividadState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {}
            }
        }
    }
}