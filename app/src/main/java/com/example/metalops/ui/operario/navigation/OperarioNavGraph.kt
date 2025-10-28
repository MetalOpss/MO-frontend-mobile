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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.metalops.core.ui.components.BottomBar
import com.example.metalops.core.ui.components.BottomNavItem
import com.example.metalops.core.ui.components.FabMenu
import com.example.metalops.ui.operario.screens.OperariosScreen
import androidx.compose.material3.Text

// Rutas específicas del rol Operario / Supervisor
object OperarioRoutes {
    const val OPERARIOS = "operario_operarios"        // lista de operarios (pantalla principal)
    const val TURNOS = "operario_turnos"              // gestión de turnos / tiempos
    const val ASIGNACIONES = "operario_asignacion"    // OTs asignadas
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperarioNavGraph() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        // Bottom bar reutilizando BottomBar genérica
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

        // Menú flotante morado (reutilizando FabMenu)
        floatingActionButton = {
            FabMenu(
                onNotificacionesClick = {
                    // navController.navigate("operario_notificaciones")
                },
                onPerfilClick = {
                    // navController.navigate("operario_perfil")
                },
                onConfiguracionClick = {
                    // navController.navigate("operario_config")
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
            // Pantalla principal: lista de operarios
            composable(OperarioRoutes.OPERARIOS) {
                OperariosScreen(navController = navController)
            }

            // Asignaciones (placeholder por ahora)
            composable(OperarioRoutes.ASIGNACIONES) {
                Text("Asignaciones de OT (TODO)")
            }

            // Turnos (placeholder por ahora)
            composable(OperarioRoutes.TURNOS) {
                Text("Gestión de turnos / tiempo (TODO)")
            }
        }
    }
}
