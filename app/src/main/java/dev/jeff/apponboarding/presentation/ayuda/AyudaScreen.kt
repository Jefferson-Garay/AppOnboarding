package dev.jeff.apponboarding.presentation.ayuda

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
data class FAQ(
    val id: String,
    val pregunta: String,
    val respuesta: String,
    val icon: ImageVector,
    val categoria: String,
    val accionDirecta: String? = null // Para navegación directa
)

data class RecursoUtil(
    val titulo: String,
    val descripcion: String,
    val icon: ImageVector,
    val tipo: String,
    val accion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyudaScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSupervisor: () -> Unit,
    onNavigateToActividades: () -> Unit,
    onNavigateToRecursos: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf("Todas") }

    // Lista de FAQs
    val faqs = remember {
        listOf(
            FAQ(
                id = "1",
                pregunta = "¿Cómo accedo a la intranet de TCS?",
                respuesta = "Puedes acceder a la intranet usando tus credenciales corporativas en el portal web. Una vez dentro, encontrarás toda la información y herramientas necesarias para tu trabajo diario.",
                icon = Icons.Default.Login,
                categoria = "Acceso"
            ),
            FAQ(
                id = "2",
                pregunta = "¿Cuándo es mi primer día de trabajo?",
                respuesta = "Tu primer día de trabajo está indicado en tu carta de oferta. También puedes verificarlo con Recursos Humanos o tu supervisor para confirmar la fecha exacta y el horario de ingreso.",
                icon = Icons.Default.CalendarToday,
                categoria = "Inicio"
            ),
            FAQ(
                id = "3",
                pregunta = "¿Cómo contacto a mi supervisor?",
                respuesta = "Puedes contactar a tu supervisor a través de la sección 'Mi Supervisor' en el menú principal de la app. Allí encontrarás su correo, teléfono y podrás iniciar un chat directo.",
                icon = Icons.Default.SupervisorAccount,
                categoria = "Contacto",
                accionDirecta = "supervisor"
            ),
            FAQ(
                id = "4",
                pregunta = "¿Dónde encuentro los formularios que necesito?",
                respuesta = "Todos los formularios y documentos importantes están disponibles en la sección 'Recursos' de la aplicación. Puedes buscar por tipo de documento o categoría.",
                icon = Icons.Default.Description,
                categoria = "Documentación",
                accionDirecta = "recursos"
            ),
            FAQ(
                id = "5",
                pregunta = "¿Cómo veo mis actividades?",
                respuesta = "Puedes ver todas tus actividades asignadas en la sección 'Mis Actividades' del menú principal. Allí verás tareas pendientes, en proceso y completadas, con sus fechas de vencimiento.",
                icon = Icons.Default.Assignment,
                categoria = "Actividades",
                accionDirecta = "actividades"
            ),
            FAQ(
                id = "6",
                pregunta = "¿Qué beneficios tengo como empleado de TCS?",
                respuesta = "Como empleado de TCS tienes acceso a: seguro médico, vacaciones pagadas, días de enfermedad, capacitación continua, y descuentos en servicios asociados. Consulta la documentación completa en Recursos.",
                icon = Icons.Default.CardGiftcard,
                categoria = "Beneficios"
            ),
            FAQ(
                id = "7",
                pregunta = "¿Cómo accedo a los cursos de capacitación?",
                respuesta = "Los cursos de capacitación están disponibles en la plataforma de aprendizaje de TCS. Revisa tus actividades asignadas para ver los cursos obligatorios, o consulta el catálogo completo en Recursos.",
                icon = Icons.Default.School,
                categoria = "Capacitación"
            ),
            FAQ(
                id = "8",
                pregunta = "¿Qué hago si tengo problemas técnicos?",
                respuesta = "Para problemas técnicos, puedes: 1) Usar el Asistente Virtual para soporte inmediato, 2) Contactar a tu supervisor, o 3) Enviar un ticket al área de Soporte Técnico. El tiempo de respuesta promedio es de 24 horas.",
                icon = Icons.Default.Build,
                categoria = "Soporte",
                accionDirecta = "chat"
            ),
            FAQ(
                id = "9",
                pregunta = "¿Cómo uso el Asistente Virtual?",
                respuesta = "El Asistente Virtual está disponible 24/7 para responder tus preguntas sobre el onboarding. Solo toca el ícono de chat en el menú principal y escribe tu consulta. El asistente aprende de tus interacciones para mejorar sus respuestas.",
                icon = Icons.Default.SmartToy,
                categoria = "Asistente",
                accionDirecta = "chat"
            ),
            FAQ(
                id = "10",
                pregunta = "¿Cómo marco mis actividades como completadas?",
                respuesta = "En la sección de Actividades, abre cualquier tarea y encontrarás un botón para cambiar su estado. También puedes hacerlo desde las notificaciones tocando el menú de opciones.",
                icon = Icons.Default.CheckCircle,
                categoria = "Actividades",
                accionDirecta = "actividades"
            )
        )
    }

    // Recursos útiles
    val recursos = remember {
        listOf(
            RecursoUtil(
                titulo = "Guía de Onboarding",
                descripcion = "Manual completo para nuevos empleados con toda la información que necesitas",
                icon = Icons.Default.MenuBook,
                tipo = "Documento",
                accion = "recursos"
            ),
            RecursoUtil(
                titulo = "Políticas de la Empresa",
                descripcion = "Conoce las normas y políticas internas de TCS",
                icon = Icons.Default.Policy,
                tipo = "Documento",
                accion = "recursos"
            ),
            RecursoUtil(
                titulo = "Videos Tutoriales",
                descripcion = "Aprende a usar las herramientas con videos paso a paso",
                icon = Icons.Default.OndemandVideo,
                tipo = "Video",
                accion = "recursos"
            ),
            RecursoUtil(
                titulo = "Directorio de Contactos",
                descripcion = "Encuentra rápidamente a las personas clave en la organización",
                icon = Icons.Default.Contacts,
                tipo = "Directorio",
                accion = "supervisor"
            )
        )
    }

    // Categorías para filtrar
    val categorias = listOf("Todas") + faqs.map { it.categoria }.distinct()

    // Filtrar FAQs
    val faqsFiltradas = faqs.filter { faq ->
        val matchCategoria = selectedCategoria == "Todas" || faq.categoria == selectedCategoria
        val matchBusqueda = searchQuery.isEmpty() ||
                faq.pregunta.contains(searchQuery, ignoreCase = true) ||
                faq.respuesta.contains(searchQuery, ignoreCase = true)
        matchCategoria && matchBusqueda
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Help,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Centro de Ayuda")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con mensaje de bienvenida
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "¿Necesitas ayuda?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Estamos aquí para apoyarte en tu proceso de onboarding",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Búsqueda
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar en preguntas frecuentes...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Filtros por categoría
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categorias.forEach { categoria ->
                            FilterChip(
                                selected = selectedCategoria == categoria,
                                onClick = { selectedCategoria = categoria },
                                label = { Text(categoria) },
                                leadingIcon = if (selectedCategoria == categoria) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }

            // Título de preguntas frecuentes
            item {
                Text(
                    text = "Preguntas Frecuentes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Lista de FAQs
            items(faqsFiltradas) { faq ->
                FAQItem(
                    faq = faq,
                    onAccionDirecta = { accion ->
                        when (accion) {
                            "supervisor" -> onNavigateToSupervisor()
                            "actividades" -> onNavigateToActividades()
                            "recursos" -> onNavigateToRecursos()
                            "chat" -> onNavigateToChat()
                        }
                    }
                )
            }

            // Mensaje si no hay resultados
            if (faqsFiltradas.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No se encontraron resultados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Intenta con otros términos de búsqueda",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Separador
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Recursos útiles
            item {
                Text(
                    text = "Recursos Útiles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    text = "Documentación, videos y guías para facilitar tu onboarding",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Lista de recursos
            items(recursos) { recurso ->
                RecursoUtilCard(
                    recurso = recurso,
                    onClick = {
                        when (recurso.accion) {
                            "supervisor" -> onNavigateToSupervisor()
                            "actividades" -> onNavigateToActividades()
                            "recursos" -> onNavigateToRecursos()
                            "chat" -> onNavigateToChat()
                        }
                    }
                )
            }

            // Card de contacto final
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ContactSupport,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "¿Aún necesitas ayuda?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Si no encontraste la respuesta que buscabas, puedes:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToChat,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.SmartToy, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Usar Asistente Virtual")
                            }

                            OutlinedButton(
                                onClick = onNavigateToSupervisor,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.SupervisorAccount, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Contactar a mi Supervisor")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FAQItem(
    faq: FAQ,
    onAccionDirecta: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            faq.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = faq.pregunta,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir"
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider()

                    Text(
                        text = faq.respuesta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Chip de categoría
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = faq.categoria,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    // Botón de acción directa si existe
                    faq.accionDirecta?.let { accion ->
                        TextButton(
                            onClick = { onAccionDirecta(accion) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ir a la sección")
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecursoUtilCard(
    recurso: RecursoUtil,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        recurso.icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = recurso.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = recurso.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = recurso.tipo,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
