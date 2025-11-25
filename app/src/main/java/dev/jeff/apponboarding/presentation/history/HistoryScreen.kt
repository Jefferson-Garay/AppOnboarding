package dev.jeff.apponboarding.presentation.history

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.ConversationHistoryItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit,
    isAdmin: Boolean
) {
    val historyState by viewModel.historyState.collectAsState()
    val stats by viewModel.statsState.collectAsState()
    val filterUsuario by viewModel.filterUsuario.collectAsState()
    val filterStartDate by viewModel.filterStartDate.collectAsState()
    val filterEndDate by viewModel.filterEndDate.collectAsState()

    val context = LocalContext.current

    // Para pruebas, ignoramos la validación de admin
    /*
    if (!isAdmin) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text("Acceso solo para administradores", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = onNavigateBack, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Volver")
                }
            }
        }
        return
    }
    */

    Scaffold(
        containerColor = Color(0xFFF5F7FA), // Fondo muy claro institucional
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    title = {
                        Text(
                            "Historial de Conversaciones",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- Indicadores de Actividad ---
            stats?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Conversaciones",
                        value = it.totalConversations.toString(),
                        icon = Icons.AutoMirrored.Filled.Chat,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Recursos",
                        value = it.totalResourcesShared.toString(),
                        icon = Icons.Default.Link,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Última actividad",
                        value = formatDateShort(it.lastConversationDate) ?: "-",
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Filtros Avanzados ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Filtros de Búsqueda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = filterUsuario,
                        onValueChange = { viewModel.updateUsuarioFilter(it) },
                        label = { Text("Buscar por usuario ID/Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DateSelectorField(
                            label = "Fecha Inicio",
                            selectedDate = filterStartDate,
                            onDateSelected = { viewModel.updateStartDate(it) },
                            modifier = Modifier.weight(1f)
                        )
                        DateSelectorField(
                            label = "Fecha Fin",
                            selectedDate = filterEndDate,
                            onDateSelected = { viewModel.updateEndDate(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text("Limpiar filtros")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { /* Lógica de exportación CSV/PDF simulada */
                                    Toast.makeText(context, "Exportando reporte...", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Exportar")
                            }
                            Button(onClick = { viewModel.loadHistory() }) {
                                Text("Aplicar Filtros")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Lista de Resultados ---
            when (historyState) {
                is HistoryState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HistoryState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Text("No se encontraron conversaciones con estos filtros", color = Color.Gray)
                        }
                    }
                }
                is HistoryState.Success -> {
                    val items = (historyState as HistoryState.Success).items
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items) { item ->
                            HistoryItemCard(item)
                        }
                    }
                }
                is HistoryState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error al cargar historial", color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun HistoryItemCard(item: ConversationHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.fecha, // Asume formato string legible
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                if (item.recursosCompartidos != null && item.recursosCompartidos > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${item.recursosCompartidos} recursos",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            
            // Usuario
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.mensajeUsuario,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(8.dp))

            // Chatbot
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.respuestaChatbot,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun DateSelectorField(
    label: String,
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Box para manejar el click, ya que OutlinedTextField deshabilitado no captura clicks bien
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedDate?.let { formatDate(it) } ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false, 
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        // Overlay transparente clickeable que cubre el TextField
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { datePickerDialog.show() }
        )
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatDateShort(dateString: String?): String? {
    // Ajustar según formato de fecha que venga del backend si es necesario
    return dateString
}
