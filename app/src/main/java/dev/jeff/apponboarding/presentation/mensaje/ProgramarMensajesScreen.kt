package dev.jeff.apponboarding.presentation.mensaje

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
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
    usuarioRef: String,
    onNavigateBack: () -> Unit
) {
    val viewModel = remember { MensajeViewModel() }
    val mensajes by viewModel.mensajesState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.opSuccess.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(usuarioRef) {
        viewModel.loadMensajes(usuarioRef)
    }

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
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (isLoading && mensajes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Historial de Mensajes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (mensajes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay mensajes programados.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(mensajes) { mensaje ->
                                MensajeItemCard(
                                    mensaje = mensaje,
                                    onDelete = { viewModel.eliminarMensaje(mensaje.id, usuarioRef) }
                                )
                            }
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
                viewModel.crearMensaje(t, d, tipo, f, h, c, cond, usuarioRef)
            }
        )
    }
}

@Composable
fun MensajeItemCard(
    mensaje: MensajeProgramadoModel,
    onDelete: () -> Unit
) {
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
                horizontalArrangement = Arrangement.SpaceBetween
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
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                }
            }
        }
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
        { _, year, month, day -> fecha = LocalDate.of(year, month + 1, day).toString() },
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
                Text("Nuevo Mensaje", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { datePickerDialog.show() }, modifier = Modifier.weight(1f)) { Text(fecha) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { timePickerDialog.show() }, modifier = Modifier.weight(1f)) { Text(hora) }
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