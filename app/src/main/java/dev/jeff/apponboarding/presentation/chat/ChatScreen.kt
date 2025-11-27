package dev.jeff.apponboarding.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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

    // Auto-scroll al fondo cuando llegan mensajes nuevos
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.SmartToy, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Asistente Virtual", style = MaterialTheme.typography.titleMedium)
                            Text("En línea", style = MaterialTheme.typography.bodySmall, color = Color.Green)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Refresh, "Limpiar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {

            // Área de mensajes
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message, usuario?.nombre ?: "Tú")
                }

                if (sendState is SendMessageState.Loading) {
                    item { TypingIndicator() }
                }
            }

            // Input Area
            Surface(
                tonalElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe una duda sobre tu onboarding...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    Spacer(Modifier.width(8.dp))

                    val isSending = sendState is SendMessageState.Loading

                    FilledIconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(
                                    usuarioRef = usuario?.id?.toString() ?: "",
                                    message = messageText
                                )
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank() && !isSending,
                        modifier = Modifier.size(50.dp)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, "Enviar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, userName: String) {
    val isMe = message.isFromUser
    val align = if (isMe) Alignment.End else Alignment.Start
    val color = if (isMe) MaterialTheme.colorScheme.primary else Color.White
    val textColor = if (isMe) Color.White else Color.Black

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Surface(
            color = color,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 16.dp
            ),
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatTime(message.timestamp),
                    color = textColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(8.dp),
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("Analizando respuesta...", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatTime(time: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
}