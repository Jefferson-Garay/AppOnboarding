package dev.jeff.apponboarding.presentation.recurso

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeff.apponboarding.data.model.RecursoModel
import dev.jeff.apponboarding.data.model.UsuarioModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursosListScreen(
    viewModel: RecursoViewModel,
    usuario: UsuarioModel? = null, // Para verificar si es admin
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val recursosState by viewModel.recursosState.collectAsState()
    val filteredRecursos by viewModel.filteredRecursos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    
    val context = LocalContext.current
    
    // Determinar si es admin (si usuario es nulo asumimos que no es admin o validamos luego,
    // ajusta según tu lógica de auth actual)
    val isAdmin = usuario?.rolRef != null // Ejemplo simple, ajustar según lógica real

    LaunchedEffect(Unit) {
        viewModel.loadRecursos()
    }

    LaunchedEffect(deleteState) {
        if (deleteState is DeleteRecursoState.Success) {
            viewModel.loadRecursos()
            viewModel.resetDeleteState()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5), // Fondo gris claro
        topBar = {
             // Header personalizado tipo HU-013 no usa TopAppBar estándar pero lo mantendremos simple
             // Si se requiere exactamente como la HU, podemos quitar la TopAppBar y hacer un header custom
             // pero para mantener consistencia con navegación usaremos un Surface header.
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onNavigateToCreate,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- Encabezado ---
            Text(
                text = "Muéstrame los formularios disponibles",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Contador de recursos ---
            val count = filteredRecursos.size
            Text(
                text = "He encontrado $count recursos para ti.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Lista de tarjetas ---
            if (recursosState is RecursosState.Loading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredRecursos.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                     Text("No se encontraron enlaces relacionados con tu búsqueda")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(filteredRecursos) { index, recurso ->
                        RecursoItemCard(
                            index = index + 1,
                            recurso = recurso,
                            onOpenLink = {
                                val url = if (!recurso.link.startsWith("http://") && !recurso.link.startsWith("https://")) {
                                    "https://${recurso.link}"
                                } else {
                                    recurso.link
                                }
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            isAdmin = isAdmin,
                            onDelete = { viewModel.deleteRecurso(recurso.id ?: "") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Barra de búsqueda inferior ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escribe 'políticas', 'formularios'...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun RecursoItemCard(
    index: Int,
    recurso: RecursoModel,
    onOpenLink: () -> Unit,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número en círculo azul
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recurso.descripcion, // Usando descripcion como título principal según el modelo
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recurso.tipo,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón abrir enlace
            IconButton(onClick = onOpenLink) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Abrir enlace",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Botón eliminar (solo si es admin)
            if (isAdmin) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de eliminar este recurso?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
