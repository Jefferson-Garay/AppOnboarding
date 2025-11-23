package dev.jeff.apponboarding.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.loginState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF002B5B),
                        Color(0xFF013A7A)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ===== TÍTULOS COMO EN LA IMAGEN =====
            Text(
                text = "Tata Consultancy Services",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Sistema de Onboarding",
                color = Color(0xFFBDD7FF),
                fontSize = 16.sp
            )

            Spacer(Modifier.height(40.dp))

            // ===== TARJETA BLANCA =====
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Bienvenido a TCS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF002B5B)
                    )

                    Text(
                        text = "Accede a tu portal de incorporación",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Correo electrónico",
                        fontWeight = FontWeight.SemiBold
                    )

                    TextField(
                        value = correo,
                        onValueChange = { correo = it },
                        placeholder = { Text("tu.email@tcs.com") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF015ECD),
                            focusedLabelColor = Color(0xFF015ECD)
                        )
                    )

                    Spacer(Modifier.height(15.dp))

                    Text(
                        text = "Contraseña",
                        fontWeight = FontWeight.SemiBold
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF015ECD),
                            focusedLabelColor = Color(0xFF015ECD)
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.login(correo, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF003F92)
                        )
                    ) {
                        Text(
                            "Iniciar Sesión",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    when (state) {
                        is LoginState.Loading -> Text("Validando...")
                        is LoginState.Error -> Text(
                            text = "Error: ${(state as LoginState.Error).message}",
                            color = Color.Red
                        )
                        is LoginState.Success -> onLoginSuccess()
                        else -> {}
                    }
                }
            }
        }
    }
}
