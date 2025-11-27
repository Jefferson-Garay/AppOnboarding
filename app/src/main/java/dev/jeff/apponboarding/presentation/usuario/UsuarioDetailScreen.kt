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

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var rolExpanded by remember { mutableStateOf(false) }
    var selectedRolName by remember { mutableStateOf("") }
    var selectedRolId by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadRoles()
        if (usuarioId != null) {
            val usuario = viewModel.getUsuarioById(usuarioId)
            usuario?.let {
                nombre = it.nombre
                correo = it.correo
                telefono = it.telefono ?: ""
                selectedRolId = it.rolRef ?: ""
            }
        }
    }

    LaunchedEffect(roles, selectedRolId) {
        if (roles.isNotEmpty() && selectedRolId.isNotBlank()) {
            val rol = roles.find { it.id == selectedRolId }
            if (rol != null) {
                selectedRolName = rol.nombre
            }
        }
    }

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
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if (usuarioId == null) "Contraseña" else "Nueva Contraseña (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

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
                        password = password,
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
                enabled = nombre.isNotBlank() && correo.isNotBlank() && selectedRolId.isNotBlank() && (usuarioId != null || password.isNotBlank())
            ) {
                Text(if (usuarioId == null) "Crear Usuario" else "Guardar Cambios")
            }
        }
    }
}