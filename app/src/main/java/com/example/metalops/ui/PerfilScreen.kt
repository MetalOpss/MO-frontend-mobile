package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    fullName: String = "Usuario MetalOps",
    email: String = "usuario@metalops.com",
    username: String = "usuariometalops"
) {
    var showChangePassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F7FA)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                AvatarHeader(fullName = fullName)

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow(label = "Correo", value = email)
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Nombre de usuario", value = username)
                        Spacer(modifier = Modifier.height(12.dp))
                        PasswordRow(label = "Contraseña", masked = "••••••••") {
                            showChangePassword = true
                            println("Abrir modal cambiar contraseña")
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        ActionButton(text = "Cerrar sesión", onClick = {
                            // logout real
                            navController.popBackStack()
                        }, tint = Color(0xFFB00020))
                    }
                }
            }

            if (showChangePassword) {
                ChangePasswordDialog(
                    onDismiss = { showChangePassword = false },
                    onConfirm = { newPass ->
                        println("Nueva contraseña: $newPass")
                        showChangePassword = false
                    }
                )
            }
        }
    }
}
