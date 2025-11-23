package dev.jeff.apponboarding.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Colores del Dashboard
val LilaFondo = Color(0xFFF3E5F5) // Fondo suave
val LilaOscuro = Color(0xFF7B1FA2) // Textos y botones

@Composable
fun HomeScreen(navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tarjeta de Bienvenida
        Card(
            colors = CardDefaults.cardColors(containerColor = LilaFondo),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo con iniciales
                Surface(
                    shape = RoundedCornerShape(50),
                    color = LilaOscuro,
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "JE", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Bienvenido/a", color = Color.Gray, fontSize = 14.sp)
                    Text(text = "jeff", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Tarjeta de Progreso
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)), // Lila muy claro
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Progreso de Onboarding", fontWeight = FontWeight.Bold, color = LilaOscuro)
                    Text(text = "0%", fontWeight = FontWeight.Bold, color = LilaOscuro)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Etapa: N/A", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 0.1f },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = LilaOscuro,
                    trackColor = Color.White,
                )
            }
        }

        Text(text = "Accesos Rápidos", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        // 3. Grid de Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón Actividades -> Navega a "actividades_list"
            QuickAccessCard(
                title = "Actividades",
                icon = Icons.Filled.Assignment,
                modifier = Modifier.weight(1f),
                onClick = { navController?.navigate("actividades_list") }
            )

            // Botón Recursos -> Navega a "recursos"
            QuickAccessCard(
                title = "Recursos",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                modifier = Modifier.weight(1f),
                onClick = { navController?.navigate("recursos") }
            )

            // Botón Roles (Placeholder)
            QuickAccessCard(
                title = "Roles",
                icon = Icons.Filled.Security,
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Navegar a roles */ }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. Footer de Contacto
        Card(
            colors = CardDefaults.cardColors(containerColor = LilaFondo),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Información de contacto", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "✉ jeff@gmail.com", fontSize = 14.sp)
                Text(text = "📞 245678", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun QuickAccessCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = modifier.aspectRatio(1f) // Cuadrado
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = LilaOscuro, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}