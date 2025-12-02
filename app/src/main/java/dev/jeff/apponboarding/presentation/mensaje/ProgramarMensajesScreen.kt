package dev.jeff.apponboarding.presentation.mensaje

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.jeff.apponboarding.data.model.*
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramarMensajesScreen(
    usuarioRef: String, // Ignoramos este parámetro ahora, usamos el selector interno
    onNavigateBack: () -> Unit
) {
    // Instanciamos el ViewModel aquí
    val viewModel = remember { MensajeViewModel() }

    val mensajes by viewModel.mensajesState.collectAsState()
    val empleados by viewModel.empleadosState.collectAsState()
    val selectedUsuario by viewModel.selectedUsuario.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.opSuccess.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    // Variables para el Dropdown de empleados
    var expandedEmpleado by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
            showCreateDialog = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Programar Mensajes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        floatingActionButton = {
            // Solo mostramos el botón si hay un usuario seleccionado
            if (selectedUsuario != null) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {

            // --- SELECTOR DE EMPLEADO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Seleccionar Empleado",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedEmpleado,
                        onExpandedChange = { expandedEmpleado = !expandedEmpleado }
                    ) {
                        OutlinedTextField(
                            value = selectedUsuario?.nombre ?: "Seleccione un empleado...",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEmpleado) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedEmpleado,
                            onDismissRequest = { expandedEmpleado = false }
                        ) {
                            empleados.forEach { empleado ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(empleado.nombre, fontWeight = FontWeight.Bold)
                                            Text(empleado.correo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectUsuario(empleado)
                                        expandedEmpleado = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- LISTA DE MENSAJES ---
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (selectedUsuario == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Selecciona un empleado para ver sus mensajes",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Text(
                    text = "Historial de Mensajes para ${selectedUsuario!!.nombre}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (mensajes.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay mensajes programados para este usuario.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(mensajes) { mensaje ->
                            MensajeItemCard(
                                mensaje = mensaje,
                                onDelete = { viewModel.eliminarMensaje(mensaje.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateMensajeDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { t, d, tipo, f, h, c, cond ->
                viewModel.crearMensaje(t, d, tipo, f, h, c, cond)
            }
        )
    }
}

@Composable
fun MensajeItemCard(
    mensaje: MensajeProgramadoModel,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mensaje.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = mensaje.estado.name,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(text = mensaje.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = try { mensaje.fechaProgramada.replace("T", " ") } catch (e: Exception) { mensaje.fechaProgramada },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar Mensaje") },
            text = { Text("¿Estás seguro? El empleado ya no verá este mensaje.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMensajeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, TipoMensaje, String, String, CanalEnvio, CondicionActivacion) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(TipoMensaje.RECORDATORIO) }
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    var hora by remember { mutableStateOf("09:00") }
    var condicion by remember { mutableStateOf(CondicionActivacion.FECHA_FIJA) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val f = LocalDate.of(year, month + 1, day)
            fecha = f.toString()
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, m -> hora = String.format("%02d:%02d", h, m) },
        9, 0, true
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Nuevo Mensaje Proactivo", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    maxLines = 3
                )

                Text("Programación:", style = MaterialTheme.typography.labelMedium)

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { datePickerDialog.show() }, modifier = Modifier.weight(1f)) {
                        Text(fecha)
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { timePickerDialog.show() }, modifier = Modifier.weight(1f)) {
                        Text(hora)
                    }
                }

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(onClick = {
                        if (titulo.isNotBlank()) onConfirm(titulo, descripcion, tipo, fecha, hora, CanalEnvio.NOTIFICACION_VISUAL, condicion)
                    }) { Text("Guardar") }
                }
            }
        }
    }
}