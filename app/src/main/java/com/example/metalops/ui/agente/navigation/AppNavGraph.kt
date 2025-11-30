package com.example.metalops.ui.agente.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.session.SessionManager
import com.example.metalops.core.ui.components.BottomBar
import com.example.metalops.core.ui.components.BottomNavItem
import com.example.metalops.core.ui.components.FabMenu
import com.example.metalops.ui.agente.screens.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    rootNavController: NavHostController,
    sessionManager: SessionManager
) {
    // NavController interno SOLO para las pantallas del agente
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                items = listOf(
                    BottomNavItem(
                        route = Destinations.HOME,
                        label = "Inicio",
                        icon = Icons.Default.Home
                    ),
                    BottomNavItem(
                        route = Destinations.CLIENTES,
                        label = "Clientes",
                        icon = Icons.Default.People
                    ),
                    BottomNavItem(
                        route = Destinations.OTS,
                        label = "OT's",
                        icon = Icons.Default.List
                    )
                ),
                startRoute = Destinations.HOME
            )
        },
        floatingActionButton = {
            FabMenu(
                onNotificacionesClick = {
                    navController.navigate(Destinations.NOTIFICACIONES)
                },
                onPerfilClick = {
                    navController.navigate(Destinations.PERFIL)
                },
                onConfiguracionClick = {
                    navController.navigate(Destinations.CONFIGURACION)
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ---------- BOTTOM BAR ----------
            composable(Destinations.HOME) {
                HomeScreen(navController)
            }

            composable(Destinations.CLIENTES) {
                ClientesScreen(navController)
            }

            composable(Destinations.OTS) {
                OrdenesTrabajoScreen(navController)
            }

            // ---------- FAB / OTRAS PANTALLAS ----------
            composable(Destinations.PERFIL) {
                ProfileScreen(
                    navController = navController,         // interno (agente)
                    rootNavController = rootNavController, // para logout
                    sessionManager = sessionManager
                )
            }

            composable(Destinations.NOTIFICACIONES) {
                NotificacionesScreen(navController)
            }

            composable(Destinations.CONFIGURACION) {
                // Configuración ahora SIN tema ni idioma global
                ConfiguracionScreen(navController)
            }

            composable(Destinations.INFO_BUILD) {
                InfoBuildScreen(navController)
            }

            composable(Destinations.INFO_TEAM) {
                InfoTeamScreen(navController)
            }

            composable(Destinations.CONTACTO) {
                ContactoScreen(navController)
            }
        }
    }
}
