package com.example.metalops.ui.admin.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.ui.components.BottomBar
import com.example.metalops.core.ui.components.BottomNavItem
import com.example.metalops.core.ui.components.FabMenu
import com.example.metalops.ui.admin.screens.AdminConfigScreen
import com.example.metalops.ui.admin.screens.AdminDashboardScreen
import com.example.metalops.ui.admin.screens.AdminUsersScreen
import com.example.metalops.ui.planner.screens.ConfiguracionPlannerScreen
import com.example.metalops.ui.planner.screens.NotificacionesPlannerScreen
import com.example.metalops.ui.planner.screens.PerfilPlannerScreen

object AdminRoutes {
    const val DASHBOARD = "admin_dashboard"
    const val USERS = "admin_users"
    const val CONFIG = "admin_config"

    const val PERFIL = "admin_perfil"
    const val NOTIFICACIONES = "admin_notificaciones"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminNavGraph(
    rootNavController: NavHostController
) {
    // nav interno solo para moverse entre tabs y pantallas de admin
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                items = listOf(
                    BottomNavItem(
                        route = AdminRoutes.DASHBOARD,
                        label = "Inicio",
                        icon = Icons.Default.Home
                    ),
                    BottomNavItem(
                        route = AdminRoutes.USERS,
                        label = "Usuarios",
                        icon = Icons.Default.Group
                    ),
                    BottomNavItem(
                        route = AdminRoutes.CONFIG,
                        label = "Config",
                        icon = Icons.Default.Settings    // <- antes era Assessment/Reportes
                    )
                ),
                startRoute = AdminRoutes.DASHBOARD
            )
        },
        floatingActionButton = {
            FabMenu(
                onNotificacionesClick = {
                    navController.navigate(AdminRoutes.NOTIFICACIONES)
                },
                onPerfilClick = {
                    navController.navigate(AdminRoutes.PERFIL)
                },
                onConfiguracionClick = {
                    navController.navigate(AdminRoutes.CONFIG)
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AdminRoutes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AdminRoutes.DASHBOARD) {
                // Usamos el rootNavController por si desde el header quieres salir a login u otro rol
                AdminDashboardScreen(navController = rootNavController)
            }

            composable(AdminRoutes.USERS) {
                AdminUsersScreen(navController = rootNavController)
            }

            composable(AdminRoutes.CONFIG) {
                AdminConfigScreen(navController = rootNavController)
            }

            // Reutilizas las pantallas del planner para perfil y notificaciones
            composable(AdminRoutes.PERFIL) {
                PerfilPlannerScreen(
                    navController = navController,
                    rootNavController = rootNavController
                )
            }

            composable(AdminRoutes.NOTIFICACIONES) {
                NotificacionesPlannerScreen(navController = navController)
            }
        }
    }
}