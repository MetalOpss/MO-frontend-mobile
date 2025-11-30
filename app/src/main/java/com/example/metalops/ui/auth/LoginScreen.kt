package com.example.metalops.ui.auth

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.metalops.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String, role: String) -> Unit,
    onForgotPasswordClick: () -> Unit // lo dejamos para no romper llamadas externas
) {
    val context = LocalContext.current
    val blue = Color(0xFF1976D2)

    // Loader para GIF animado
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estado para el diálogo de "olvidé mi contraseña"
    var showResetDialog by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun doLogin() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Ingresa correo y contraseña"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    isLoading = false
                    errorMessage = "Usuario inválido"
                    return@addOnSuccessListener
                }

                val uid = user.uid
                val currentEmail = user.email ?: email

                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        isLoading = false

                        if (doc != null && doc.exists()) {
                            val rawRole =
                                doc.getString("rol")
                                    ?: doc.getString("role")
                                    ?: "agente"

                            val roleFinal = when (rawRole.lowercase()) {
                                "planner" -> "Planner"
                                "operario" -> "Operario"
                                "agente" -> "Agente"
                                "admin" -> "Admin"
                                else -> "Agente"
                            }

                            Log.d("LOGIN", "Usuario: $currentEmail, rol=$roleFinal")
                            onLoginClick(currentEmail, password, roleFinal)
                        } else {
                            isLoading = false
                            errorMessage = "No se encontró el usuario en Firestore"
                            Log.e("LOGIN", "Documento /users/$uid no existe")
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        Log.e("LOGIN", "Error al obtener rol", e)
                        errorMessage = "Error al obtener rol"
                    }
            }
            .addOnFailureListener { e ->
                isLoading = false
                Log.e("LOGIN", "Error de autenticación", e)
                errorMessage = "Correo o contraseña inválidos"
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // GIF animado del logo
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.drawable.metalops_anim)
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = "Logo animado MetalOps",
                    modifier = Modifier.size(180.dp)
                )

                Text(
                    text = "MetalOps",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Inicia sesión",
                    style = MaterialTheme.typography.titleLarge
                )

                // CORREO
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                // CONTRASEÑA
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // OLVIDÉ MI CONTRASEÑA -> abre diálogo de cambio
                TextButton(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = blue
                    )
                ) {
                    Text("Olvidé mi contraseña")
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red
                    )
                }

                // BOTÓN LOGIN
                Button(
                    onClick = { doLogin() },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text("Iniciar Sesión")
                    }
                }
            }

            // ========= DIÁLOGO CAMBIAR CONTRASEÑA =========
            if (showResetDialog) {
                var resetEmail by remember { mutableStateOf(email) }
                var currentPassword by remember { mutableStateOf("") }
                var newPassword by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var isProcessing by remember { mutableStateOf(false) }
                var dialogError by remember { mutableStateOf<String?>(null) }
                var showPasswords by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = {
                        if (!isProcessing) showResetDialog = false
                    },
                    title = { Text("Cambiar contraseña") },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Para cambiar tu contraseña, ingresa tu correo, tu contraseña actual y la nueva contraseña.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )

                            OutlinedTextField(
                                value = resetEmail,
                                onValueChange = { resetEmail = it },
                                label = { Text("Correo") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Contraseña actual") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                visualTransformation = if (showPasswords)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Nueva contraseña") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                visualTransformation = if (showPasswords)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirmar nueva contraseña") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                visualTransformation = if (showPasswords)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextButton(
                                onClick = { showPasswords = !showPasswords }
                            ) {
                                Text(
                                    if (showPasswords) "Ocultar contraseñas"
                                    else "Mostrar contraseñas"
                                )
                            }

                            dialogError?.let {
                                Text(
                                    text = it,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val correo = resetEmail.trim()
                                val oldPass = currentPassword
                                val newPass = newPassword
                                val confirmPass = confirmPassword

                                if (correo.isBlank() || oldPass.isBlank() ||
                                    newPass.isBlank() || confirmPass.isBlank()
                                ) {
                                    dialogError = "Completa todos los campos"
                                    return@TextButton
                                }

                                if (newPass != confirmPass) {
                                    dialogError = "La nueva contraseña no coincide"
                                    return@TextButton
                                }

                                isProcessing = true
                                dialogError = null

                                // 1) Verificamos credenciales
                                auth.signInWithEmailAndPassword(correo, oldPass)
                                    .addOnSuccessListener { result ->
                                        val user = result.user
                                        if (user == null) {
                                            isProcessing = false
                                            dialogError = "Usuario inválido"
                                            return@addOnSuccessListener
                                        }

                                        // 2) Actualizamos la contraseña
                                        user.updatePassword(newPass)
                                            .addOnSuccessListener {
                                                isProcessing = false
                                                Toast.makeText(
                                                    context,
                                                    "Contraseña actualizada. Inicia sesión con tu nueva contraseña.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                // Opcional: cerrar sesión para volver al login limpio
                                                auth.signOut()
                                                showResetDialog = false
                                            }
                                            .addOnFailureListener { e ->
                                                isProcessing = false
                                                dialogError = e.localizedMessage
                                                    ?: "Error al actualizar la contraseña"
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        isProcessing = false
                                        dialogError = e.localizedMessage
                                            ?: "Correo o contraseña actual incorrectos"
                                    }
                            },
                            enabled = !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Cambiar contraseña")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                if (!isProcessing) showResetDialog = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
