package dev.jeff.apponboarding.presentation.Dash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.jeff.apponboarding.data.repository.ActividadRepository

@Composable
fun DashboardScreen(
    navController: NavController,
    usuarioRef: String,
    repository: ActividadRepository, // ✅ CORREGIDO: Recibe Repository en lugar de Service
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository) // ✅ Pasamos el repositorio a la Factory
    )
) {
    // Colección de estados del ViewModel
    val global by viewModel.globalResumen.collectAsState()
    val usuario by viewModel.usuarioResumen.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()

    // Estado para el scroll de la pantalla
    val scrollState = rememberScrollState()

    // Cargar datos al entrar
    LaunchedEffect(usuarioRef) {
        // Solo cargamos si no hay datos para evitar recargas innecesarias
        if (global == null && usuario == null) {
            viewModel.cargarDashboard(usuarioRef)
        }
    }

    Scaffold(
        topBar = {
            // Opcional: Puedes poner una TopAppBar aquí si quieres
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Muestra carga centrada en toda la pantalla
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMsg != null) {
                // Muestra error si falla
                Text(
                    text = errorMsg ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Muestra el contenido con Scroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState), // Habilita scroll vertical
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reporte de Avance",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(24.dp))

                    // --- SECCIÓN GLOBAL ---
                    Text(
                        text = "Global de la Empresa",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))

                    if (global != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            BarChartView(
                                rango0 = global!!.rango_0_25,
                                rango26 = global!!.rango_26_50,
                                rango51 = global!!.rango_51_75,
                                rango76 = global!!.rango_76_100,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp) // Altura fija obligatoria para el gráfico
                                    .padding(16.dp)
                            )
                        }
                    } else {
                        Text("No hay datos globales disponibles")
                    }

                    Spacer(Modifier.height(32.dp))

                    // --- SECCIÓN USUARIO ---
                    Text(
                        text = "Tu Rendimiento",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))

                    if (usuario != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            BarChartView(
                                rango0 = usuario!!.rango_0_25,
                                rango26 = usuario!!.rango_26_50,
                                rango51 = usuario!!.rango_51_75,
                                rango76 = usuario!!.rango_76_100,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp) // Altura fija obligatoria
                                    .padding(16.dp)
                            )
                        }
                    } else {
                        Text("No hay datos de usuario disponibles")
                    }

                    // Espacio final para que no quede pegado al borde inferior al hacer scroll
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}