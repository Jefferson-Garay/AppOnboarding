package dev.jeff.apponboarding.presentation.home


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Modelo para los items del menú lateral (drawer)
 * Usado en HomeEmpleadoScreen y HomeAdminScreen
 */
data class DrawerMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

/**
 * Card de acceso rápido (cuadrada)
 * Usado en HomeEmpleadoScreen y HomeAdminScreen
 */
@Composable
fun QuickAccessCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    color: Color = Color(0xFFEBE8F2)
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF5E5375)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black
            )
        }
    }
}

/**
 * Popup de mensaje animado
 * Usado en HomeEmpleadoScreen
 */
@Composable
fun MensajePopup(
    mensaje: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mensaje,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                    contentDescription = "Cerrar"
                )
            }
        }
    }
}