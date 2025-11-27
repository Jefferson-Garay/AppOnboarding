package dev.jeff.apponboarding.presentation.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.jeff.apponboarding.data.model.ResumenGlobalResponse
import dev.jeff.apponboarding.data.model.UsuarioModel // Importar el modelo de usuario real

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    // Observar el estado global (gráfico) y el estado de usuarios (lista)
    val resumenGlobalState by viewModel.resumenGlobalState.collectAsState()
    val usuariosState by viewModel.usuariosState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadResumenGlobal()
        viewModel.loadUsuarios() // <--- Cargar la lista de usuarios reales
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard de Avances", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // =========================================================
            // SECCIÓN 1: GRÁFICO DE BARRAS VERTICAL (DISTRIBUCIÓN GLOBAL)
            // =========================================================
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Distribución de Progreso Global", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(16.dp))

                        when (val state = resumenGlobalState) {
                            is ResumenGlobalState.Loading -> Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                            is ResumenGlobalState.Error -> Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) { Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error) }
                            is ResumenGlobalState.Success -> VerticalBarChart(resumen = state.resumen)
                        }
                    }
                }
            }

            // =========================================================
            // SECCIÓN 2: AVANCE INDIVIDUAL DE EMPLEADOS (LISTA DINÁMICA)
            // =========================================================
            item {
                Text("Avance por Empleado (Usuarios)", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                // Mostrar lista dinámica basada en el estado
                when (val state = usuariosState) {
                    is UsuariosDashboardState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is UsuariosDashboardState.Error -> {
                        Text("Error al cargar empleados: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                    is UsuariosDashboardState.Success -> {
                        Column {
                            state.usuarios.forEach { usuario ->
                                UserProgressCard(
                                    usuario = usuario,
                                    onClick = { /* Implementar navegación a detalle de usuario */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// COMPONENTE: UserProgressCard (Utiliza UsuarioModel real)
// ---------------------------------------------------------------------
@Composable
fun UserProgressCard(usuario: UsuarioModel, onClick: () -> Unit) {
    // Extraer el porcentaje real del objeto nivelOnboarding, usando 0 como fallback
    val progress = usuario.nivelOnboarding?.porcentaje ?: 0
    val nombre = usuario.nombre

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono del usuario
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(16.dp))

                Column {
                    // Muestra el nombre real del usuario
                    Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))

                    // Barra de progreso lineal
                    LinearProgressIndicator(
                        progress = progress.toFloat() / 100f,
                        modifier = Modifier.width(150.dp).clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                }
            }

            // Porcentaje y flecha
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = "Detalles",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// COMPONENTE: VerticalBarChart (Gráfico de barras)
// ---------------------------------------------------------------------
@Composable
fun VerticalBarChart(resumen: ResumenGlobalResponse) {
    val data = listOf(
        Pair("0-25%", resumen.rango_0_25),
        Pair("26-50%", resumen.rango_26_50),
        Pair("51-75%", resumen.rango_51_75),
        Pair("76-100%", resumen.rango_76_100)
    )
    val maxCount = data.maxOfOrNull { it.second } ?: 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, count) ->
            val barHeightFraction by animateFloatAsState(
                targetValue = if (maxCount > 0) count.toFloat() / maxCount else 0f,
                animationSpec = tween(durationMillis = 1000)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight(barHeightFraction)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }

    // Etiquetas de las categorías en la parte inferior
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        data.forEach { (label, _) ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}