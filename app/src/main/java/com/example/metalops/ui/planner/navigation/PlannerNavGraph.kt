package com.example.metalops.ui.planner.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.metalops.core.ui.components.BottomBar
import com.example.metalops.core.ui.components.BottomNavItem
import com.example.metalops.core.ui.components.FabMenu
import com.example.metalops.ui.planner.screens.ConfiguracionPlannerScreen
import com.example.metalops.ui.planner.screens.NotificacionesPlannerScreen
import com.example.metalops.ui.planner.screens.PerfilPlannerScreen
import com.example.metalops.ui.planner.screens.PlannerHomeScreen
import com.example.metalops.ui.planner.screens.PlannerOrderDetailScreen
import com.example.metalops.ui.planner.screens.PlannerOrdersScreen
import com.example.metalops.ui.planner.screens.PlannerTimelineScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlannerNavGraph(
    rootNavController: NavHostController   // nav raíz (RootNavGraph)
) {

    // NavController interno del rol Planner
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

            // ------- Pantallas principales (BottomBar) -------
            composable(PlannerRoutes.DASHBOARD) {
                PlannerHomeScreen(navController = navController)
            }

            composable(PlannerRoutes.OTS) {
                PlannerOrdersScreen(navController = navController)
            }

            composable(PlannerRoutes.TIEMPO) {
                PlannerTimelineScreen(navController = navController)
            }

            // ------- Detalle / planificación de una OT -------
            composable(
                route = "planner_order_detail/{orderId}",
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable

                PlannerOrderDetailScreen(
                    navController = navController,
                    workOrderId = orderId,
                    workOrderTitle = "",   // se usa como fallback dentro de la pantalla
                    clientName = ""        // se usa como fallback dentro de la pantalla
                )
            }

            // ------- Pantallas que abre el FAB -------
            composable(PlannerRoutes.PERFIL) {
                PerfilPlannerScreen(
                    navController = navController,          // nav interno
                    rootNavController = rootNavController   // nav raíz (para logout)
                )
            }

            composable(PlannerRoutes.NOTIFICACIONES) {
                NotificacionesPlannerScreen(navController = navController)
            }

            composable(PlannerRoutes.CONFIG) {
                ConfiguracionPlannerScreen(navController = navController)
            }
        }
    }
}
