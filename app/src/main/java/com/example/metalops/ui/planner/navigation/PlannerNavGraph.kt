package com.example.metalops.ui.planner.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.metalops.core.ui.components.FabMenu
import com.example.metalops.core.ui.components.BottomBar
import com.example.metalops.core.ui.components.BottomNavItem
import com.example.metalops.ui.planner.screens.HomeDashboard
import com.example.metalops.ui.planner.screens.PlannerOTsScreen
import com.example.metalops.ui.planner.screens.PlannerTiempoScreen
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.AccessTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlannerNavGraph() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                items = listOf(
                    BottomNavItem(
                        route = PlannerRoutes.DASHBOARD,
                        label = "Inicio",
                        icon = Icons.Default.Home
                    ),
                    BottomNavItem(
                        route = PlannerRoutes.OTS,
                        label = "OT's",
                        icon = Icons.Default.List
                    ),
                    BottomNavItem(
                        route = PlannerRoutes.TIEMPO,
                        label = "Tiempo",
                        icon = Icons.Default.AccessTime
                    )
                ),
                startRoute = PlannerRoutes.DASHBOARD
            )
        },
        floatingActionButton = {
            FabMenu(
                onNotificacionesClick = {
                    navController.navigate(PlannerRoutes.NOTIFICACIONES)
                },
                onPerfilClick = {
                    navController.navigate(PlannerRoutes.PERFIL)
                },
                onConfiguracionClick = {
                    navController.navigate(PlannerRoutes.CONFIG)
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PlannerRoutes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantalla inicial (home/dashboard del planner)
            composable(PlannerRoutes.DASHBOARD) {
                HomeDashboard(navController = navController)
            }

            // OTs del planner
            composable(PlannerRoutes.OTS) {
                PlannerOTsScreen(navController = navController)
            }

            // Tiempo del planner
            composable(PlannerRoutes.TIEMPO) {
                PlannerTiempoScreen(navController = navController)
            }

            // Perfil (placeholder por ahora)
            composable(PlannerRoutes.PERFIL) {
                Text("Perfil Planner (TODO)")
            }

            // Notificaciones (placeholder)
            composable(PlannerRoutes.NOTIFICACIONES) {
                Text("Notificaciones Planner (TODO)")
            }

            // Configuración (placeholder)
            composable(PlannerRoutes.CONFIG) {
                Text("Configuración Planner (TODO)")
            }
        }
    }
}
