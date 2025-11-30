package com.example.metalops.ui.operario.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
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
import com.example.metalops.ui.operario.screens.AsignacionesScreen
import com.example.metalops.ui.operario.screens.ConfiguracionOperarioScreen
import com.example.metalops.ui.operario.screens.OperarioNotificationsScreen
import com.example.metalops.ui.operario.screens.OperarioProfileScreen
// ⬇️ CORRIGE ESTE IMPORT
import com.example.metalops.ui.planner.screens.OperariosScreen
import com.example.metalops.ui.operario.screens.TurnosOperarioScreen

object OperarioRoutes {
    const val OPERARIOS = "operario_operarios"
    const val ASIGNACIONES = "operario_asignacion"
    const val TURNOS = "operario_turnos"

    const val PERFIL = "operario_perfil"
    const val NOTIFICACIONES = "operario_notificaciones"
    const val CONFIG = "operario_config"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperarioNavGraph() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                items = listOf(
                    BottomNavItem(
                        route = OperarioRoutes.OPERARIOS,
                        label = "Operarios",
                        icon = Icons.Default.Group
                    ),
                    BottomNavItem(
                        route = OperarioRoutes.ASIGNACIONES,
                        label = "Asignaciones",
                        icon = Icons.Default.List
                    ),
                    BottomNavItem(
                        route = OperarioRoutes.TURNOS,
                        label = "Turnos",
                        icon = Icons.Default.Schedule
                    )
                ),
                startRoute = OperarioRoutes.OPERARIOS
            )
        },
        floatingActionButton = {
            FabMenu(
                onNotificacionesClick = {
                    navController.navigate(OperarioRoutes.NOTIFICACIONES)
                },
                onPerfilClick = {
                    navController.navigate(OperarioRoutes.PERFIL)
                },
                onConfiguracionClick = {
                    navController.navigate(OperarioRoutes.CONFIG)
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = OperarioRoutes.OPERARIOS,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(OperarioRoutes.OPERARIOS) {
                OperariosScreen(navController = navController)
            }

            composable(OperarioRoutes.ASIGNACIONES) {
                AsignacionesScreen(navController = navController)
            }

            composable(OperarioRoutes.TURNOS) {
                TurnosOperarioScreen(navController = navController)
            }

            composable(OperarioRoutes.PERFIL) {
                OperarioProfileScreen(navController = navController)
            }

            composable(OperarioRoutes.NOTIFICACIONES) {
                OperarioNotificationsScreen(navController = navController)
            }

            composable(OperarioRoutes.CONFIG) {
                ConfiguracionOperarioScreen(navController = navController)
            }
        }
    }
}