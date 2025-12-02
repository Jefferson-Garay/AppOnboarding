package dev.jeff.apponboarding.presentation.auth

import android.util.Log
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeff.apponboarding.data.model.UsuarioModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccessEmpleado: () -> Unit,  // ⭐ NUEVO: Navega a home empleado
    onLoginSuccessAdmin: () -> Unit      // ⭐ NUEVO: Navega a home admin
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.loginState.collectAsState()

    // ⭐ NUEVO: Detectar rol y navegar según corresponda
    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            val usuario = (state as LoginState.Success).user as? UsuarioModel

            usuario?.let { user ->
                // 1) Primero intentamos con el nombre del rol (vía login)
                val rolNombre = user.rolNombre ?: ""

                // 2) Si quisieras seguir usando rolRef cuando venga del GET:
                val rolId = user.rolRef ?: ""

                val esAdmin =
                    rolNombre.equals("Administrador", ignoreCase = true) || // login
                            rolId == "6913adbcca79acfd93858d5c"                     // por id

                if (esAdmin) {
                    onLoginSuccessAdmin()
                } else {
                    onLoginSuccessEmpleado()
                }
            }
        }
    }


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
                        ),
                        singleLine = true
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
                        visualTransformation = PasswordVisualTransformation(),  // ⭐ NUEVO: Oculta contraseña
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF015ECD),
                            focusedLabelColor = Color(0xFF015ECD)
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (correo.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(correo, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF003F92)
                        ),
                        enabled = correo.isNotBlank() && password.isNotBlank()  // ⭐ NUEVO: Validación
                    ) {
                        Text(
                            "Iniciar Sesión",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    when (state) {
                        is LoginState.Loading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Validando...")
                            }
                        }
                        is LoginState.Error -> Text(
                            text = (state as LoginState.Error).message,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth()
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}