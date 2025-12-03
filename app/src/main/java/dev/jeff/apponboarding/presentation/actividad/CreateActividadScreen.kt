package dev.jeff.apponboarding.presentation.actividad

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.ActividadRequest
import dev.jeff.apponboarding.data.model.UsuarioModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActividadScreen(
    viewModel: ActividadViewModel,
    onNavigateBack: () -> Unit
) {
    // Estados del formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("tarea") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }

    // Estados para selección de usuario
    var selectedUsuario by remember { mutableStateOf<UsuarioModel?>(null) }
    var expandedUsuario by remember { mutableStateOf(false) }

    val usuarios by viewModel.usuariosState.collectAsState()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Cargar usuarios al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadUsuarios()
    }

    // DatePicker Helpers
    val datePickerDialogInicio = DatePickerDialog(
        context,
        { _, year, month, day ->
            // Ajustamos el mes (Calendar va de 0-11) y formateamos a ISO (yyyy-MM-dd)
            val date = LocalDate.of(year, month + 1, day)
            fechaInicio = date.format(DateTimeFormatter.ISO_DATE)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val datePickerDialogFin = DatePickerDialog(
        context,
        { _, year, month, day ->
            val date = LocalDate.of(year, month + 1, day)
            fechaFin = date.format(DateTimeFormatter.ISO_DATE)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Lógica para saber si el formulario es válido
    val isFormValid = titulo.isNotEmpty() &&
            selectedUsuario != null &&
            fechaInicio.isNotEmpty() &&
            fechaFin.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Actividad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                )
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
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Text("Detalles de la Actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    // Selección de Empleado (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = expandedUsuario,
                        onExpandedChange = { expandedUsuario = !expandedUsuario }
                    ) {
                        OutlinedTextField(
                            value = selectedUsuario?.nombre ?: "Seleccionar Empleado",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Asignar a") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUsuario) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedUsuario,
                            onDismissRequest = { expandedUsuario = false }
                        ) {
                            if (usuarios.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Cargando usuarios...") },
                                    onClick = { }
                                )
                            } else {
                                usuarios.forEach { usuario ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(usuario.nombre, fontWeight = FontWeight.Bold)
                                                Text(usuario.correo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                            }
                                        },
                                        onClick = {
                                            selectedUsuario = usuario
                                            expandedUsuario = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título *") },
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

                    // Tipo de Actividad (Dropdown simple)
                    var expandedTipo by remember { mutableStateOf(false) }
                    val tipos = listOf("tarea", "documento", "video", "reunion")

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = tipo.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                            modifier = Modifier.fillMaxWidth().clickable { expandedTipo = true },
                            enabled = false, // Deshabilitamos input directo, usamos click en Box
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = Color.LightGray,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        // Overlay transparente para capturar el click
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { expandedTipo = true }
                        )

                        DropdownMenu(
                            expanded = expandedTipo,
                            onDismissRequest = { expandedTipo = false }
                        ) {
                            tipos.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.uppercase()) },
                                    onClick = {
                                        tipo = t
                                        expandedTipo = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = fechaInicio,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Inicio *") },
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialogInicio.show() }) {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = fechaFin,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fin *") },
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialogFin.show() }) {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (isFormValid) {
                        // Corrección: Aseguramos que se envíen Strings, no nulls
                        val actividad = ActividadRequest(
                            titulo = titulo,
                            descripcion = descripcion,
                            tipo = tipo,
                            // Se añaden horas fijas para cumplir formato ISO Date-Time
                            fechaInicio = if (fechaInicio.isNotEmpty()) "${fechaInicio}T09:00:00" else "",
                            fechaFin = if (fechaFin.isNotEmpty()) "${fechaFin}T18:00:00" else "",
                            usuarioRef = selectedUsuario!!.obtenerIdReal(),
                            estado = "pendiente"
                        )
                        viewModel.createActividad(actividad) {
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = isFormValid // Solo se habilita si todo está completo
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Crear Actividad")
            }
        }
    }
}