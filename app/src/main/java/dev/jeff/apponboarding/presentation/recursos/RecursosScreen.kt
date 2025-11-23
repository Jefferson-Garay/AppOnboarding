package dev.jeff.apponboarding.presentation.recursos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.jeff.apponboarding.data.model.Recurso
import dev.jeff.apponboarding.presentation.home.AzulOscuro
import dev.jeff.apponboarding.presentation.home.FondoGris

@Composable
fun RecursosScreen(viewModel: RecursosViewModel = viewModel()) {
    // Solución Definitiva (Parte 3): Cargar datos solo cuando la pantalla es visible.
    LaunchedEffect(Unit) {
        viewModel.cargarRecursos()
    }

    val recursosAgrupados by viewModel.recursosAgrupados.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    val utiles = listOf(
        "https://portal.tcs.com",
        "https://learning.tcs.com",
        "https://support.tcs.com"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGris)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "URLs útiles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
            Spacer(modifier = Modifier.height(8.dp))
            utiles.forEach { url ->
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { val intent = Intent(Intent.ACTION_VIEW, url.toUri()); context.startActivity(intent) }.padding(vertical = 4.dp)
                )
            }
        }

        if (error != null) {
            item {
                Text(text = error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        recursosAgrupados.keys.filterNotNull().sorted().forEach { tipo ->
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)) {
                    Box(modifier = Modifier.size(4.dp, 24.dp).background(AzulOscuro))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tipo.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                }
            }
            items(recursosAgrupados[tipo] ?: emptyList()) { recurso ->
                RecursoCard(recurso = recurso, onCardClick = {
                    recurso.link?.takeIf { it.isNotBlank() }?.let { safeUrl ->
                        val intent = Intent(Intent.ACTION_VIEW, safeUrl.toUri())
                        context.startActivity(intent)
                    }
                })
            }
        }
    }
}

@Composable
private fun RecursoCard(recurso: Recurso, onCardClick: () -> Unit) {
    val (title, subtitle) = remember(recurso.descripcion) {
        val parts = (recurso.descripcion ?: "").split('|', limit = 2)
        val t = parts.getOrNull(0) ?: (recurso.descripcion ?: "")
        val s = parts.getOrNull(1) ?: ""
        t to s
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForType(recurso.tipo),
                    contentDescription = null,
                    tint = AzulOscuro,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = AzulOscuro)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Abrir enlace", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 20.sp)
                }
            }
        }
    }
}

private fun getIconForType(tipo: String?): ImageVector {
    return when (tipo?.lowercase()) {
        "recursos" -> Icons.Default.Person
        "documentos" -> Icons.Default.Description
        else -> Icons.Default.Link
    }
}
