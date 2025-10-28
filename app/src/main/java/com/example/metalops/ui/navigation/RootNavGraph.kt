package com.example.metalops.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.metalops.core.session.SessionManager
import com.example.metalops.ui.agente.navigation.AppNavGraph
import com.example.metalops.ui.planner.navigation.PlannerNavGraph
import kotlinx.coroutines.launch

// rutas principales
sealed class RootRoute(val route: String) {
    object Auth : RootRoute("auth")
    object Planner : RootRoute("planner")
    object Agente : RootRoute("agente")
    object Operario : RootRoute("operario")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = RootRoute.Auth.route // login si no hay sesión
    ) {

        // ===== AUTH FLOW =====
        authNavGraph(
            navController = navController,
            onLoginSuccess = { role ->
                // 1. guardar sesión
                scope.launch {
                    sessionManager.saveUserRole(role)
                }

                // 2. navegar según rol
                when (role) {
                    "Planner" -> {
                        navController.navigate(RootRoute.Planner.route) {
                            popUpTo(RootRoute.Auth.route) { inclusive = true }
                        }
                    }
                    "Agente" -> {
                        navController.navigate(RootRoute.Agente.route) {
                            popUpTo(RootRoute.Auth.route) { inclusive = true }
                        }
                    }
                    "Operario" -> {
                        navController.navigate(RootRoute.Operario.route) {
                            popUpTo(RootRoute.Auth.route) { inclusive = true }
                        }
                    }
                }
            }
        )

        // ===== PLANNER MODULE =====
        composable(RootRoute.Planner.route) {
            PlannerNavGraph()
        }

        // ===== AGENTE MODULE =====
        composable(RootRoute.Agente.route) {
            AppNavGraph(
                rootNavController = navController,   // 👈 le pasamos el navController raíz
                sessionManager = sessionManager
            )
        }

        // ===== OPERARIO MODULE (placeholder) =====
        composable(RootRoute.Operario.route) {
            PlannerNavGraph()
        }
    }
}
