package dev.jeff.apponboarding.presentation.recurso

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jeff.apponboarding.data.model.RecursoRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecursoScreen(
    viewModel: RecursoViewModel,
    adminRef: String,
    onNavigateBack: () -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }

    val createState by viewModel.createState.collectAsState()

    LaunchedEffect(createState) {
        if (createState is CreateRecursoState.Success) {
            viewModel.resetCreateState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Recurso") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("Enlace/URL") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://ejemplo.com/recurso") },
                singleLine = true
            )

            // Selector de tipo
            var expanded by remember { mutableStateOf(false) }
            val tipos = listOf("pdf", "doc", "video", "imagen", "formulario", "enlace", "otro")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tipos.forEach { tipoOption ->
                        DropdownMenuItem(
                            text = { Text(tipoOption) },
                            onClick = {
                                tipo = tipoOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Información del tipo de recurso
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Los recursos creados estarán disponibles para todos los usuarios del sistema.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val recurso = RecursoRequest(
                        descripcion = descripcion,
                        link = link,
                        tipo = tipo,
                        adminRef = adminRef
                    )
                    viewModel.createRecurso(recurso)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = descripcion.isNotBlank() && link.isNotBlank() && tipo.isNotBlank()
            ) {
                Text("Crear Recurso")
            }

            when (createState) {
                is CreateRecursoState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                is CreateRecursoState.Error -> {
                    Text(
                        text = (createState as CreateRecursoState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is CreateRecursoState.Success -> {
                    Text(
                        text = (createState as CreateRecursoState.Success).message,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {}
            }
        }
    }
}