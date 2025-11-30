package com.example.metalops.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.metalops.core.session.SessionManager
import com.example.metalops.ui.agente.navigation.AppNavGraph
import com.example.metalops.ui.auth.LoginScreen
import com.example.metalops.ui.operario.navigation.OperarioNavGraph
import com.example.metalops.ui.planner.navigation.PlannerNavGraph
import com.example.metalops.ui.admin.navigation.AdminNavGraph  // 👈 IMPORTA EL NAVGRAPH, NO EL DASHBOARD

// Rutas principales
sealed class RootRoute(val route: String) {
    object Auth     : RootRoute("auth")
    object Planner  : RootRoute("planner")
    object Agente   : RootRoute("agente")
    object Operario : RootRoute("operario")
    object Admin    : RootRoute("admin")    // 👈 ya tenías esto
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: String = RootRoute.Auth.route,
    sessionManager: SessionManager
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ---------- LOGIN ----------
        composable(RootRoute.Auth.route) {
            LoginScreen(
                onLoginClick = { email, _, role ->

                    val roleNorm = role.lowercase().trim()
                    val adminEmail = "admin@metalops.com"   // cambia por tu correo admin real

                    val isAdmin =
                        roleNorm == "admin" ||
                                email.equals(adminEmail, ignoreCase = true)

                    if (isAdmin) {
                        navController.navigate(RootRoute.Admin.route) {
                            popUpTo(RootRoute.Auth.route) { inclusive = true }
                        }
                        return@LoginScreen
                    }

                    val target = when (roleNorm) {
                        "planner"  -> RootRoute.Planner.route
                        "operario" -> RootRoute.Operario.route
                        else       -> RootRoute.Agente.route
                    }

                    navController.navigate(target) {
                        popUpTo(RootRoute.Auth.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    // recuperación si quieres luego
                }
            )
        }

        // ---------- NAVGRAPH ADMIN (AQUÍ VA EL BOTTOM BAR) ----------
        composable(RootRoute.Admin.route) {
            AdminNavGraph(rootNavController = navController)
        }

        // ---------- NAVGRAPH AGENTE ----------
        composable(RootRoute.Agente.route) {
            AppNavGraph(
                rootNavController = navController,
                sessionManager = sessionManager
            )
        }

        // ---------- NAVGRAPH PLANNER ----------
        composable(RootRoute.Planner.route) {
            PlannerNavGraph(rootNavController = navController)
        }

        // ---------- NAVGRAPH OPERARIO ----------
        composable(RootRoute.Operario.route) {
            OperarioNavGraph()
        }
    }
}