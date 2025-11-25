package dev.jeff.apponboarding.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
                                text = "Asistente Virtual",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (sendState is SendMessageState.Loading) "Escribiendo..." else "En línea",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (sendState is SendMessageState.Loading)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color(0xFF4CAF50)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Limpiar chat")
                    }
                    IconButton(onClick = { /* TODO: Más opciones */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                            Text("Conectando al asistente...")
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
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // --- Sección de bienvenida estilizada (si es el primer mensaje) ---
                        // Verificamos si el primer mensaje es del sistema y lo personalizamos
                        // O agregamos un header si la lista no está vacía
                        
                        if (messages.isEmpty()) {
                             item {
                                 WelcomeHeader(onSuggestionClick = { text ->
                                     messageText = text
                                 })
                             }
                        } else if (messages.size == 1 && !messages[0].isFromUser && messages[0].id.startsWith("welcome")) {
                             item {
                                 WelcomeHeader(onSuggestionClick = { text ->
                                     messageText = text
                                 })
                             }
                        }

                        items(messages) { message ->
                            // No mostrar el mensaje de bienvenida default si ya tenemos el header custom
                            if (!message.id.startsWith("welcome")) {
                                ChatBubble(
                                    message = message,
                                    userName = usuario?.nombre ?: "Usuario"
                                )
                            }
                        }

                        // Indicador de escribiendo
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

@Composable
fun WelcomeHeader(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.SmartToy, 
                    contentDescription = null, 
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Text(
            "¡Hola! Soy tu asistente de onboarding.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            "Puedo ayudarte con temas como intranet, políticas, formularios y capacitación.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(Modifier.height(8.dp))
        
        // Sugerencias
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2
        ) {
            SuggestionChip(onClick = { onSuggestionClick("¿Dónde encuentro las políticas?") }, label = { Text("Políticas") })
            Spacer(Modifier.width(8.dp))
            SuggestionChip(onClick = { onSuggestionClick("Necesito un formulario") }, label = { Text("Formularios") })
            Spacer(Modifier.width(8.dp))
            SuggestionChip(onClick = { onSuggestionClick("Información sobre capacitación") }, label = { Text("Capacitación") })
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
    val bubbleColor = if (isFromUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isFromUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isFromUser) {
            // Avatar del bot
            Surface(
                modifier = Modifier
                    .size(32.dp)
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
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isFromUser) 16.dp else 4.dp,
                    bottomEnd = if (isFromUser) 4.dp else 16.dp
                ),
                tonalElevation = 1.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Hora del mensaje
            Text(
                text = formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        if (isFromUser) {
            Spacer(Modifier.width(8.dp))
            // Avatar del usuario
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.secondary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
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
            modifier = Modifier
                .size(32.dp)
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
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.width(8.dp))

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
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
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Campo de texto
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("ej.: 'políticas', 'formularios'...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                maxLines = 4,
                enabled = !isLoading
            )

            // Botón de enviar
            FilledIconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isLoading,
                modifier = Modifier.size(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Enviar"
                    )
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
