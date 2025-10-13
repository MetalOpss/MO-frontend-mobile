package com.example.metalops.ui.planner.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.ui.components.PlannerBottomBar
import com.example.metalops.ui.planner.screens.HomeDashboard
import com.example.metalops.ui.planner.screens.PlannerOTsScreen
import com.example.metalops.ui.planner.screens.PlannerTiempoScreen

object PlannerDestinations {
    const val HOME_DASHBOARD = "home_dashboard"
    const val OTS = "planner_ots"
    const val TIEMPO = "planner_tiempo"
}

@Composable
fun PlannerNavGraph(modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = { PlannerBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PlannerDestinations.HOME_DASHBOARD,
            modifier = modifier.padding(innerPadding)
        ) {
            // 🏠 Pantalla de Inicio
            composable(PlannerDestinations.HOME_DASHBOARD) {
                HomeDashboard(navController)
            }

            // 📋 Pantalla de OT's
            composable(PlannerDestinations.OTS) {
                PlannerOTsScreen(navController)
            }

            // ⏱️ Pantalla de Tiempo
            composable(PlannerDestinations.TIEMPO) {
                PlannerTiempoScreen(navController)
            }
        }
    }
}
