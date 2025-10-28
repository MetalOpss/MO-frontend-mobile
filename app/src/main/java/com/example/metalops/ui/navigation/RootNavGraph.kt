package com.example.metalops.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.ui.agente.navigation.AppNavGraph
import com.example.metalops.ui.planner.navigation.PlannerNavGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "auth" // 🔑 Empieza en autenticación
    ) {
        // 🔐 Grafo de Autenticación
        authNavGraph(
            navController = navController,
            onLoginSuccess = { role ->
                // Navegar según el rol seleccionado
                when (role) {
                    "Planner" -> {
                        navController.navigate("planner") {
                            // Limpiar el stack de auth para que no pueda volver
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                    "Agente" -> {
                        navController.navigate("agente") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                    "Operario" -> {
                        navController.navigate("operario") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
            }
        )

        // 📊 Módulo Planner
        composable("planner") {
            PlannerNavGraph()
        }

        // 👤 Módulo Agente
        composable("agente") {
            AppNavGraph()
        }

        // 🔧 Módulo Operario (cuando lo crees)
        composable("operario") {
            // TODO: Crear OperarioNavGraph()
            // Por ahora, mostrar el planner como placeholder
            PlannerNavGraph()
        }
    }
}