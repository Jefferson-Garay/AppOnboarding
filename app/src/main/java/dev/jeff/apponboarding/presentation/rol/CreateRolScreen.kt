package dev.jeff.apponboarding.presentation.rol

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.RolRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRolScreen(
    viewModel: RolViewModel,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var permisos by remember { mutableStateOf(mutableListOf<String>()) }
    var showAddPermisoDialog by remember { mutableStateOf(false) }

    val createState by viewModel.createState.collectAsState()

    LaunchedEffect(createState) {
        if (createState is CreateRolState.Success) {
            viewModel.resetCreateState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Rol") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPermisoDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar permiso")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Rol") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej: Administrador, Empleado") }
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("Describe las responsabilidades de este rol") }
            )

            // Lista de permisos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Permisos (${permisos.size})",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(8.dp))

                    if (permisos.isEmpty()) {
                        Text(
                            text = "No hay permisos agregados. Toca el botón + para agregar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(permisos) { permiso ->
                                PermisoChip(
                                    permiso = permiso,
                                    onRemove = { permisos.remove(permiso) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val rol = RolRequest(
                        nombre = nombre,
                        descripcion = descripcion,
                        permisos = permisos.toList()
                    )
                    viewModel.createRol(rol)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && descripcion.isNotBlank() && permisos.isNotEmpty()
            ) {
                Text("Crear Rol")
            }

            when (createState) {
                is CreateRolState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is CreateRolState.Error -> {
                    Text(
                        text = (createState as CreateRolState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {}
            }
        }
    }

    // Dialog para agregar permiso
    if (showAddPermisoDialog) {
        AddPermisoDialog(
            onDismiss = { showAddPermisoDialog = false },
            onAdd = { permiso ->
                if (permiso.isNotBlank() && !permisos.contains(permiso)) {
                    permisos.add(permiso)
                }
                showAddPermisoDialog = false
            }
        )
    }
}

@Composable
fun PermisoChip(
    permiso: String,
    onRemove: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = permiso,
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar",
                modifier = Modifier
                    .size(16.dp)
                    .padding(0.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPermisoDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var permisoText by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    // Permisos sugeridos comunes
    val permisosSugeridos = listOf(
        "crear_usuario",
        "editar_usuario",
        "eliminar_usuario",
        "ver_usuarios",
        "gestionar_recursos",
        "crear_recursos",
        "editar_recursos",
        "eliminar_recursos",
        "ver_recursos",
        "asignar_actividades",
        "completar_actividades",
        "ver_actividades",
        "gestionar_roles",
        "consultar_chatbot",
        "ver_reportes",
        "generar_reportes"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Permiso") },
        text = {
            Column {
                OutlinedTextField(
                    value = permisoText,
                    onValueChange = {
                        permisoText = it
                        showSuggestions = it.isNotBlank()
                    },
                    label = { Text("Nombre del permiso") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ej: crear_usuario") }
                )

                if (showSuggestions) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Sugerencias:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(Modifier.height(4.dp))

                    val filteredSuggestions = permisosSugeridos.filter {
                        it.contains(permisoText, ignoreCase = true)
                    }.take(5)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        filteredSuggestions.forEach { sugerencia ->
                            TextButton(
                                onClick = {
                                    permisoText = sugerencia
                                    showSuggestions = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = sugerencia,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(permisoText) },
                enabled = permisoText.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}