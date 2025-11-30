package com.example.metalops.ui.operario.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperarioProfileScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Datos del usuario
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    val rol = "Operario"

    // Cambio de contraseña
    var nuevaPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val isPasswordValid = nuevaPassword.length >= 6

    // Cargar datos desde Firebase + SessionManager
    LaunchedEffect(Unit) {
        val authUser = FirebaseAuth.getInstance().currentUser
        val nameFromAuth = authUser?.displayName
        val emailFromAuth = authUser?.email

        val nameFromSession = sessionManager.getUserName()
        val emailFromSession = sessionManager.getUserEmail()

        usuario = (nameFromAuth ?: nameFromSession).orEmpty()
        correo = (emailFromAuth ?: emailFromSession).orEmpty()
    }

    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    // ---- MODAL ----
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Perfil",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Información de la cuenta ($rol)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1E3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (usuario.isNotBlank())
                            usuario.first().uppercase()
                        else
                            "?",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (usuario.isNotBlank()) {
                    Text(
                        text = usuario,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(text = "Rol: $rol", color = Color.Gray)

                // Correo
                if (correo.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF4F4F4), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFF0F1E3A)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Correo", color = Color.Gray)
                            Text(correo, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Cambiar contraseña
                Text(
                    text = "Cambiar contraseña",
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = nuevaPassword,
                    onValueChange = {
                        nuevaPassword = it
                        passwordError = null
                    },
                    placeholder = { Text("Nueva contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = passwordError != null
                )

                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            if (!isPasswordValid) {
                                passwordError = "Mínimo 6 caracteres"
                                return@launch
                            }

                            val user = FirebaseAuth.getInstance().currentUser
                            if (user == null) {
                                showToast("No hay usuario autenticado")
                                return@launch
                            }

                            try {
                                isLoading = true
                                user.updatePassword(nuevaPassword).await()
                                nuevaPassword = ""
                                showToast("Contraseña actualizada correctamente")
                            } catch (e: Exception) {
                                passwordError = e.message ?: "Error al actualizar contraseña"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = isPasswordValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Guardar contraseña")
                    }
                }

                // ---- CERRAR SESIÓN ----
                TextButton(
                    onClick = {
                        scope.launch {
                            // 1) Limpiar sesión local
                            sessionManager.clearSession()
                            // 2) Cerrar sesión en Firebase
                            FirebaseAuth.getInstance().signOut()
                            // 3) Reiniciar la app (volver a la pantalla inicial, normalmente login)
                            activity?.let { act ->
                                val launchIntent: Intent? =
                                    act.packageManager.getLaunchIntentForPackage(act.packageName)
                                launchIntent?.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                )
                                if (launchIntent != null) {
                                    act.startActivity(launchIntent)
                                }
                                act.finish()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cerrar sesión",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}