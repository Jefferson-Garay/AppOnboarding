package dev.jeff.apponboarding.presentation.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.UsuarioRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioDetailScreen(
    viewModel: UsuarioViewModel,
    usuarioId: String? = null,
    onNavigateBack: () -> Unit
) {
    val roles by viewModel.rolesState.collectAsState()
    val opStatus by viewModel.opStatus.collectAsState()

    // Variables de estado para el formulario
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    // Variables para el Dropdown
    var rolExpanded by remember { mutableStateOf(false) }
    var selectedRolName by remember { mutableStateOf("Seleccionar Rol") }
    var selectedRolId by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Cargar datos iniciales
    LaunchedEffect(Unit) {
        viewModel.loadRoles() // Cargar lista de roles para el dropdown

        // Si estamos en modo EDICIÓN (usuarioId no es nulo)
        if (usuarioId != null) {
            val usuarioEncontrado = viewModel.getUsuarioById(usuarioId)

            usuarioEncontrado?.let { user ->
                // Rellenamos los campos con la info del usuario
                nombre = user.nombre
                correo = user.correo
                telefono = user.telefono ?: ""

                // Guardamos el ID del rol para buscar su nombre después
                user.rolRef?.let { rolRef ->
                    selectedRolId = rolRef
                }
            }
        }
    }

    // 2. Efecto para actualizar el nombre del Rol en el Dropdown
    // Se ejecuta cuando cargan los roles o cuando cambia el rol seleccionado
    LaunchedEffect(roles, selectedRolId) {
        if (roles.isNotEmpty() && selectedRolId.isNotBlank()) {
            val rolObj = roles.find { it.id == selectedRolId }
            if (rolObj != null) {
                selectedRolName = rolObj.nombre
            }
        }
    }

    // 3. Manejo de mensajes de éxito/error
    LaunchedEffect(opStatus) {
        opStatus?.let {
            snackbarHostState.showSnackbar(it)
            if (it.contains("exitosamente") || it.contains("actualizado")) {
                viewModel.clearStatus()
                onNavigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (usuarioId == null) "Nuevo Empleado" else "Editar Empleado") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Correo
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Contraseña (Opcional en edición)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if (usuarioId == null) "Contraseña" else "Nueva Contraseña (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = { if (usuarioId != null) Text("Dejar en blanco para mantener actual") }
            )

            // Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            // Dropdown de Rol
            ExposedDropdownMenuBox(
                expanded = rolExpanded,
                onExpandedChange = { rolExpanded = !rolExpanded }
            ) {
                OutlinedTextField(
                    value = selectedRolName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol Asignado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rolExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = rolExpanded,
                    onDismissRequest = { rolExpanded = false }
                ) {
                    roles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol.nombre) },
                            onClick = {
                                selectedRolName = rol.nombre
                                selectedRolId = rol.id ?: ""
                                rolExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val request = UsuarioRequest(
                        nombre = nombre,
                        correo = correo,
                        password = password, // El backend debe saber ignorarla si está vacía en update
                        rolRef = selectedRolId,
                        telefono = telefono
                    )

                    if (usuarioId == null) {
                        viewModel.createUsuario(request)
                    } else {
                        viewModel.updateUsuario(usuarioId, request)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // Validación: Se habilita si hay datos básicos. Password obligatoria solo al crear.
                enabled = nombre.isNotBlank() && correo.isNotBlank() && selectedRolId.isNotBlank() && (usuarioId != null || password.isNotBlank())
            ) {
                Text(if (usuarioId == null) "Crear Usuario" else "Guardar Cambios")
            }
        }
    }
}