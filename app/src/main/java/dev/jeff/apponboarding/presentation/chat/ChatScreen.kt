package dev.jeff.apponboarding.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.UsuarioModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    usuario: UsuarioModel?,
    onNavigateBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val sendState by viewModel.sendState.collectAsState()
    val salaState by viewModel.salaState.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // CAMBIO: Panel oculto por defecto (false)
    var isPanelVisible by remember { mutableStateOf(false) }

    // Cargar o crear sala al iniciar
    LaunchedEffect(usuario) {
        usuario?.let {
            viewModel.loadOrCreateSala(
                usuarioRef = it.id?.toString() ?: "",
                nombre = it.nombre,
                correo = it.correo,
                area = it.area ?: "",
                rolRef = it.rolRef ?: ""
            )
        }
    }

    // Scroll automático al nuevo mensaje
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar del bot
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Agente de Onboarding",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = if (sendState is SendMessageState.Loading) "Escribiendo..." else "En línea",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    // Botón para alternar el panel lateral
                    IconButton(onClick = { isPanelVisible = !isPanelVisible }) {
                        Icon(
                            if (isPanelVisible) Icons.Default.VisibilityOff else Icons.Default.Info,
                            contentDescription = if (isPanelVisible) "Ocultar info" else "Ver info",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Limpiar chat", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- PANEL IZQUIERDO: INFORMACIÓN DEL USUARIO (PLEGABLE) ---
            AnimatedVisibility(
                visible = isPanelVisible,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()), // Permitir scroll si la info es larga
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Información del Usuario",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // DATOS REALES DEL BACKEND (UsuarioModel)
                        UserInfoItem(
                            label = "Nombre Completo",
                            value = usuario?.nombre ?: "Desconocido"
                        )

                        UserInfoItem(
                            label = "Correo Electrónico",
                            value = usuario?.correo ?: "No registrado"
                        )

                        if (!usuario?.area.isNullOrBlank()) {
                            UserInfoItem(
                                label = "Área / Departamento",
                                value = usuario?.area ?: ""
                            )
                        }

                        if (!usuario?.telefono.isNullOrBlank()) {
                            UserInfoItem(
                                label = "Teléfono",
                                value = usuario?.telefono ?: ""
                            )
                        }

                        if (!usuario?.rolRef.isNullOrBlank()) {
                            UserInfoItem(
                                label = "Rol Asignado",
                                value = usuario?.rolRef ?: "N/A"
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text(
                            text = "Estado de Onboarding",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        UserInfoItem(
                            label = "Etapa Actual",
                            value = usuario?.nivelOnboarding?.etapa ?: "Inicio"
                        )

                        Column {
                            Text(
                                text = "Progreso: ${usuario?.nivelOnboarding?.porcentaje ?: 0}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = (usuario?.nivelOnboarding?.porcentaje ?: 0) / 100f,
                                modifier = Modifier.fillMaxWidth(),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        UserInfoItem(
                            label = "Última Actualización",
                            value = usuario?.nivelOnboarding?.ultimaActualizacion ?: "Reciente"
                        )
                        
                        UserInfoItem(
                            label = "Estado Cuenta",
                            value = usuario?.estado ?: "Activo",
                            valueColor = if (usuario?.estado == "Activo") Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                    
                    // Divisor vertical
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }

            // --- PANEL DERECHO: CHAT (EXPANDIBLE) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Estado de carga de sala
                when (salaState) {
                    is SalaState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator()
                                Text("Conectando al asistente...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    is SalaState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = (salaState as SalaState.Error).message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    else -> {
                        // Lista de mensajes
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Mensaje de bienvenida personalizado
                            if (messages.isEmpty()) {
                                item {
                                    InitialWelcomeMessage(usuario?.nombre ?: "Usuario")
                                }
                            } else if (messages.size == 1 && !messages[0].isFromUser && messages[0].id.startsWith("welcome")) {
                                item {
                                    InitialWelcomeMessage(usuario?.nombre ?: "Usuario")
                                }
                            }

                            items(messages) { message ->
                                if (!message.id.startsWith("welcome")) {
                                    ChatBubble(
                                        message = message,
                                        userName = usuario?.nombre ?: "Usuario"
                                    )
                                }
                            }

                            if (sendState is SendMessageState.Loading) {
                                item {
                                    TypingIndicator()
                                }
                            }
                        }

                        // Campo de entrada de mensaje
                        MessageInput(
                            value = messageText,
                            onValueChange = { messageText = it },
                            onSend = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(
                                        usuarioRef = usuario?.id?.toString() ?: "",
                                        message = messageText
                                    )
                                    messageText = ""
                                }
                            },
                            isLoading = sendState is SendMessageState.Loading
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoItem(
    label: String, 
    value: String, 
    subValue: String? = null,
    valueColor: Color? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
        if (subValue != null) {
            Text(
                text = subValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InitialWelcomeMessage(userName: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¡Bienvenido/a, $userName!",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Soy tu asistente de onboarding. Puedes preguntarme sobre tus tareas, recursos o cualquier duda sobre la empresa.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    userName: String
) {
    val isFromUser = message.isFromUser
    // Colores estilo imagen, adaptables al tema
    val bubbleColor = if (isFromUser) {
        MaterialTheme.colorScheme.primary // Azul en light, azul claro en dark
    } else {
        MaterialTheme.colorScheme.surface // Blanco en light, gris oscuro en dark
    }
    val textColor = if (isFromUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val alignment = if (isFromUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isFromUser) 16.dp else 4.dp,
                bottomEnd = if (isFromUser) 4.dp else 16.dp
            ),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(16.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    ) {}
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 4,
                enabled = !isLoading
            )

            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isLoading
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
