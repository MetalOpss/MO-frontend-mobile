package com.example.metalops.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MetalOpsApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // estado donde guardamos el rol almacenado (o null si no había sesión)
    var storedRole by remember { mutableStateOf<String?>(null) }
    var isLoaded by remember { mutableStateOf(false) }

    // leer DataStore una sola vez al inicio
    LaunchedEffect(Unit) {
        storedRole = sessionManager.userRoleFlow.first() // suspending read
        isLoaded = true
    }

    // Mientras cargamos sesión, no mostramos todavía NavHost para evitar parpadeo
    if (!isLoaded) {
        // Aquí podrías poner un SplashScreen composable si quieres estilo "MetalOps"
        return
    }

    // Si ya había sesión guardada -> navega directo al módulo adecuado
    LaunchedEffect(storedRole) {
        when (storedRole) {
            "Planner" -> {
                navController.navigate(RootRoute.Planner.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            "Agente" -> {
                navController.navigate(RootRoute.Agente.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            "Operario" -> {
                navController.navigate(RootRoute.Operario.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            null -> {
                // no había sesión -> se queda en auth/login
            }
        }
    }

    // Montamos el grafo raíz y le pasamos sessionManager
    RootNavGraph(
        navController = navController,
        sessionManager = sessionManager
    )
}
