package com.example.metalops.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.session.SessionManager
import kotlinx.coroutines.flow.first

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MetalOpsApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var storedRole by remember { mutableStateOf<String?>(null) }
    var isLoaded by remember { mutableStateOf(false) }

    // ✅ lee la sesión desde DataStore (propiedad correcta: userRole)
    LaunchedEffect(Unit) {
        storedRole = sessionManager.userRole.first()
        isLoaded = true
    }

    if (!isLoaded) {
        // aquí podrías mostrar un Splash composable si deseas
        return
    }

    // Si hay sesión previa, navega directo al módulo y limpia el back stack
    LaunchedEffect(storedRole) {
        when (storedRole) {
            "Planner" -> {
                navController.navigate(RootRoute.Planner.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
            "Agente" -> {
                navController.navigate(RootRoute.Agente.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
            "Operario" -> {
                navController.navigate(RootRoute.Operario.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
            null -> {
                // sin sesión: se queda en auth/login
            }
        }
    }

    RootNavGraph(
        navController = navController,
        sessionManager = sessionManager
    )
}
