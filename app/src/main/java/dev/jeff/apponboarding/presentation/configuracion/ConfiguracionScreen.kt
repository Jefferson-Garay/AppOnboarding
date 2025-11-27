package dev.jeff.apponboarding.presentation.configuracion

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    viewModel: ConfiguracionViewModel,
    usuarioId: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    areNotificationsEnabled: Boolean,
    onToggleNotifications: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val changePasswordState by viewModel.changePasswordState.collectAsState()

    // Estados para diálogos
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    
    // Variables para el formulario de contraseña
    var newPassword by remember { mutableStateOf("") }

    // Efecto para mostrar Toast según resultado
    LaunchedEffect(changePasswordState) {
        when (changePasswordState) {
            is ChangePasswordState.Success -> {
                Toast.makeText(context, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                showPasswordDialog = false
                newPassword = ""
                viewModel.resetState()
            }
            is ChangePasswordState.Error -> {
                Toast.makeText(context, (changePasswordState as ChangePasswordState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false 
                newPassword = ""
                viewModel.resetState()
            },
            title = { Text("Cambiar Contraseña") },
            text = { 
                Column {
                    Text("Ingresa tu nueva contraseña:")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (changePasswordState is ChangePasswordState.Loading) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.changePassword(usuarioId, newPassword)
                    },
                    enabled = newPassword.isNotBlank() && changePasswordState !is ChangePasswordState.Loading
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showPasswordDialog = false 
                        newPassword = ""
                        viewModel.resetState()
                    },
                    enabled = changePasswordState !is ChangePasswordState.Loading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacidad") },
            text = { Text("Aquí podrías configurar quién ve tu perfil, estado, etc.\n\nConfiguración simulada.") },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConfiguracionSection("General") {
                ConfiguracionSwitchItem(
                    title = "Notificaciones",
                    description = "Recibir alertas sobre nuevas actividades y mensajes.",
                    checked = areNotificationsEnabled,
                    onCheckedChange = { 
                        onToggleNotifications()
                    },
                    icon = Icons.Default.Notifications
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                ConfiguracionSwitchItem(
                    title = "Modo Oscuro",
                    description = "Cambiar la apariencia de la aplicación.",
                    checked = isDarkTheme,
                    onCheckedChange = { 
                        onToggleTheme()
                    },
                    icon = Icons.Default.DarkMode
                )
            }

            ConfiguracionSection("Cuenta") {
                ConfiguracionActionItem(
                    title = "Cambiar Contraseña",
                    description = "Actualiza tu clave de acceso.",
                    icon = Icons.Default.Lock,
                    onClick = { showPasswordDialog = true }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                ConfiguracionActionItem(
                    title = "Privacidad",
                    description = "Gestiona quién puede ver tu información.",
                    icon = Icons.Default.Security,
                    onClick = { showPrivacyDialog = true }
                )
            }
        }
    }
}

@Composable
fun ConfiguracionSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ConfiguracionSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ConfiguracionActionItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
