package com.example.metalops.ui.agente.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.metalops.core.session.SessionManager
import com.example.metalops.data.remote.FirebaseAuthRepository
import com.example.metalops.ui.navigation.RootRoute
import kotlinx.coroutines.launch
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun ProfileScreen(
    navController: NavHostController,      // nav interno del agente
    rootNavController: NavHostController,  // nav raíz (para logout)
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepo = remember { FirebaseAuthRepository(context) }

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    // Colores base (azul MetalOps)
    val primaryBlue = Color(0xFF1976D2)

    // Cargar datos de sesión
    LaunchedEffect(Unit) {
        userName = sessionManager.getUserName() ?: ""
        userEmail = sessionManager.getUserEmail() ?: ""
    }

    // ---------- MODAL FULLSCREEN ----------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)) // fondo oscuro semi-transparente
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Cerrar modal si tocan fuera
                navController.popBackStack()
            },
        contentAlignment = Alignment.Center
    ) {
        // ---------- CARD DEL MODAL ----------
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* consumir click para que no cierre */ },
                horizontalAlignment = Alignment.Start
            ) {

                // ---------- HEADER DEL MODAL ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Perfil",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ---------- INFO USUARIO ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Usuario",
                        tint = primaryBlue,
                        modifier = Modifier.size(56.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (userName.isNotBlank()) userName else "Usuario MetalOps",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = userEmail,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Título sección contraseña
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = primaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cambiar contraseña",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                PasswordField(
                    label = "Contraseña actual",
                    value = currentPass,
                    onValueChange = { currentPass = it },
                    primaryBlue = primaryBlue
                )

                PasswordField(
                    label = "Nueva contraseña",
                    value = newPass,
                    onValueChange = { newPass = it },
                    primaryBlue = primaryBlue
                )

                PasswordField(
                    label = "Confirmar contraseña",
                    value = confirmPass,
                    onValueChange = { confirmPass = it },
                    primaryBlue = primaryBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (currentPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (newPass != confirmPass) {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            loading = true
                            val result = authRepo.changePassword(currentPass, newPass)
                            loading = false

                            result.onSuccess {
                                Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_LONG).show()
                                currentPass = ""
                                newPass = ""
                                confirmPass = ""
                            }

                            result.onFailure {
                                Toast.makeText(
                                    context,
                                    it.message ?: "Error al actualizar contraseña",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    )
                ) {
                    Text(
                        text = if (loading) "Guardando..." else "Guardar cambios",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- BOTÓN CERRAR SESIÓN ----------
                Button(
                    onClick = {
                        scope.launch {
                            authRepo.logout()
                            rootNavController.navigate(RootRoute.Auth.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB71C1C)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Cerrar sesión", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    primaryBlue: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        visualTransformation = PasswordVisualTransformation(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryBlue,
            cursorColor = primaryBlue
        )
    )
}
