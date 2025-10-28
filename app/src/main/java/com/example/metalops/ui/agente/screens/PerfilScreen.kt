package com.example.metalops.ui.agente.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.ChangePasswordDialog
import com.example.metalops.core.ui.components.*
import com.example.metalops.core.session.SessionManager
import com.example.metalops.ui.agente.navigation.Destinations
import com.example.metalops.ui.navigation.RootRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavHostController,         // nav interno del módulo Agente
    rootNavController: NavHostController,     // nav raíz (RootNavGraph)
    sessionManager: SessionManager,
    modifier: Modifier = Modifier,
    fullName: String = "Usuario MetalOps",
    email: String = "usuario@metalops.com",
    username: String = "usuariometalops"
) {
    var showChangePassword by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                AvatarHeader(fullName = fullName)

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
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
                        }
                        Spacer(modifier = Modifier.height(18.dp))

                        // Botón "Cerrar sesión"
                        ActionButton(
                            text = "Cerrar sesión",
                            onClick = {
                                scope.launch {
                                    // 1. Borrar la sesión guardada (rol)
                                    sessionManager.clearSession()

                                    // 2. Asegurar que el nav interno del agente
                                    //    vuelve a un estado base (por ejemplo HOME),
                                    //    y salimos de pantallas profundas como Perfil.
                                    navController.popBackStack(
                                        route = Destinations.HOME,
                                        inclusive = false
                                    )

                                    // 3. Ir al flujo de login usando el nav raíz (global)
                                    rootNavController.navigate(RootRoute.Auth.route) {
                                        // limpiar TODO el back stack previo (agente, planner, etc.)
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            tint = Color(0xFFB00020)
                        )
                    }
                }
            }

            if (showChangePassword) {
                ChangePasswordDialog(
                    onDismiss = { showChangePassword = false },
                    onConfirm = { newPass ->
                        // TODO: implementar cambio de contraseña real
                        showChangePassword = false
                    }
                )
            }
        }
    }
}
