package dev.jeff.apponboarding.presentation.actividad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.ui.theme.FondoGris
import dev.jeff.apponboarding.ui.theme.VerdeExito
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesListScreen(
    viewModel: ActividadViewModel,
    usuarioRef: String,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {

    val state by viewModel.actividadesState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadActividadesByUsuario(usuarioRef)
    }

    LaunchedEffect(deleteState) {
        if (deleteState is DeleteActividadState.Success) {
            viewModel.loadActividadesByUsuario(usuarioRef)
            viewModel.resetDeleteState()
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UpdateActividadState.Success) {
            viewModel.loadActividadesByUsuario(usuarioRef)
            viewModel.resetUpdateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Actividades") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar actividad")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FondoGris)
        ) {

            when (state) {

                is ActividadesState.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ActividadesState.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("Error al cargar actividades")
                    }
                }

                is ActividadesState.Idle -> {
                    // Estado inicial vacío
                }

                is ActividadesState.Success -> {

                    val actividades = (state as ActividadesState.Success).actividades

                    // ---------- PROGRESO Y PRÓXIMOS PASOS ----------
                    val total = actividades.size
                    val completadas = actividades.count { it.estado.equals("completada", true) }
                    val progreso = if (total > 0) (completadas * 100 / total) else 0

                    val proximas =
                        actividades.filter { !it.estado.equals("completada", true) }
                            .sortedBy { it.fechaInicio }
                            .take(3)

                    // -------- TARJETA PROFESIONAL: PROGRESO --------
                    SectionCard(title = "Calendario de Actividades") {

                        LinearProgressIndicator(
                            progress = progreso / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "$progreso% completado",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // -------- TARJETA PROFESIONAL: PRÓXIMOS PASOS --------
                    SectionCard(title = "Próximos pasos") {
                        if (proximas.isEmpty()) {
                            Text("No hay actividades pendientes.", color = Color.Gray)
                        } else {
                            proximas.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(item.titulo, fontSize = 15.sp)
                                }
                            }
                        }
                    }

                    // -------- TÍTULO LISTA --------
                    Text(
                        "Lista de actividades",
                        Modifier.padding(start = 16.dp, top = 20.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // -------- LISTA COMPLETA --------
                    if (actividades.isEmpty()) {
                        Box(Modifier.fillMaxWidth(), Alignment.Center) {
                            Text("No tienes actividades aún")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(actividades) { actividad ->

                                ActividadCard(
                                    actividad = actividad,
                                    onToggle = {
                                        val nuevoEstado =
                                            if (actividad.estado.equals("completada", true))
                                                "pendiente"
                                            else "completada"

                                        actividad.id?.let {
                                            viewModel.updateEstado(it, nuevoEstado)
                                        }
                                    },
                                    onClick = {
                                        onNavigateToDetail(actividad.id ?: "")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ---------- INDICADOR DE CARGA ----------
            if (deleteState is DeleteActividadState.Loading ||
                updateState is UpdateActividadState.Loading
            ) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
    }
}

/* ============================================================
   TARJETA DE SECCIÓN
============================================================ */
@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            content()
        }
    }
}

/* ============================================================
   CARD DE ACTIVIDAD
============================================================ */
@Composable
fun ActividadCard(
    actividad: ActividadModel,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = actividad.estado.equals("completada", true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = VerdeExito,
                    checkmarkColor = Color.White
                )
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = actividad.titulo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    InfoChip(
                        text = actividad.tipo,
                        icon = Icons.Default.CalendarToday
                    )

                    val fecha = try {
                        OffsetDateTime.parse(actividad.fechaInicio)
                            .format(DateTimeFormatter.ofPattern("dd MMM"))
                    } catch (_: Exception) {
                        "S/F"
                    }

                    InfoChip(
                        text = fecha,
                        icon = Icons.Default.Schedule
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    actividad.descripcion,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/* ============================================================
   CHIP
============================================================ */
@Composable
fun InfoChip(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}
