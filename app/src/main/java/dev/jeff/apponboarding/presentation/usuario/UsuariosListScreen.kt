package dev.jeff.apponboarding.presentation.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.RolModel
import dev.jeff.apponboarding.data.model.UsuarioModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosListScreen(
    viewModel: UsuarioViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val usuarios by viewModel.usuariosState.collectAsState()
    val roles by viewModel.rolesState.collectAsState() // Necesitamos los roles para saber los nombres
    val isLoading by viewModel.isLoading.collectAsState()
    val opStatus by viewModel.opStatus.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadUsuarios()
        viewModel.loadRoles() // IMPORTANTE: Cargar roles para poder mostrar sus nombres
    }

    LaunchedEffect(opStatus) {
        opStatus?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Gestión de Empleados") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Crear Usuario")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && usuarios.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (usuarios.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay usuarios registrados")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(usuarios) { usuario ->
                            UsuarioItemCard(
                                usuario = usuario,
                                roles = roles, // Pasamos la lista de roles
                                onEdit = {
                                    val idLimpio = usuario.obtenerIdReal()
                                    if (idLimpio.isNotBlank()) onNavigateToEdit(idLimpio)
                                },
                                onDelete = {
                                    val idLimpio = usuario.obtenerIdReal()
                                    if (idLimpio.isNotBlank()) viewModel.deleteUsuario(idLimpio)
                                }
                            )
                        }
                    }
                }
            }

            if (isLoading && usuarios.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun UsuarioItemCard(
    usuario: UsuarioModel,
    roles: List<RolModel>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Lógica para encontrar el nombre del rol
    val nombreRol = remember(usuario.rolRef, roles) {
        roles.find { it.id == usuario.rolRef }?.nombre ?: "Sin Rol Asignado"
    }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Usamos las iniciales si están disponibles
                    val iniciales = usuario.nombre.take(2).uppercase()
                    Text(text = iniciales, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = usuario.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = usuario.correo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                // AQUÍ MOSTRAMOS EL ROL EN LUGAR DEL ÁREA
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = nombreRol,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Estás seguro de eliminar a ${usuario.nombre}?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}