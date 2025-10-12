package com.example.metalops.ui.agente.navigation

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.ui.components.BottomBar
import com.example.metalops.ui.agente.screens.*
import android.os.Build

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantallas principales
            composable(Destinations.HOME) { HomeScreen(navController) }
            composable(Destinations.CLIENTES) { ClientesScreen(navController) }
            composable(Destinations.OTS) { OrdenesTrabajoScreen(navController) }

            // Registrar / editar cliente
            composable(Destinations.REGISTRAR_CLIENTE) {
                RegistrarClienteScreen(
                    onClose = { navController.popBackStack() },
                    onAction = { navController.popBackStack() }
                )
            }
            composable(Destinations.EDITAR_CLIENTE) {
                EditarClienteScreen(
                    onClose = { navController.popBackStack() },
                    onAction = { navController.popBackStack() }
                )
            }

            // Rutas del FAB
            composable(Destinations.PERFIL) { PerfilScreen(navController) }
            composable(Destinations.NOTIFICACIONES) { NotificacionesScreen(navController) }
            composable(Destinations.CONFIGURACION) { ConfiguracionScreen(navController) }

            // Info / contacto
            composable(Destinations.INFO_BUILD) { InfoBuildScreen(navController) }
            composable(Destinations.INFO_TEAM)  { InfoTeamScreen(navController) }
            composable(Destinations.CONTACTO)   { ContactoScreen(navController) }

            // Flujo de creación de OT
            composable(Destinations.CREAR_OT_PASO1) {
                CrearOTPaso1Screen(
                    onSiguiente = { navController.navigate(Destinations.CREAR_OT_PASO2) },
                    onCerrar = { navController.navigate(Destinations.OTS) }
                )
            }
            composable(Destinations.CREAR_OT_PASO2) {
                CrearOTPaso2Screen(
                    onSiguiente = { navController.navigate(Destinations.CREAR_OT_PASO3) },
                    onAtras = { navController.popBackStack() },
                    onCerrar = { navController.navigate(Destinations.OTS) }
                )
            }
            composable(Destinations.CREAR_OT_PASO3) {
                CrearOTPaso3Screen(
                    onSiguiente = { navController.navigate(Destinations.CREAR_OT_PASO3_1) },
                    onAtras = { navController.popBackStack() },
                    onCerrar = { navController.navigate(Destinations.OTS) }
                )
            }
            composable(Destinations.CREAR_OT_PASO3_1) {
                CrearOTPaso3_1Screen(
                    onAtras = { navController.popBackStack(Destinations.CREAR_OT_PASO3, inclusive = false) },
                    onSiguiente = { navController.navigate(Destinations.CREAR_OT_PASO4) },
                    onCerrar = { navController.navigate(Destinations.OTS) }
                )
            }
            composable(Destinations.CREAR_OT_PASO4) {
                CrearOTPaso4Screen(
                    onAtras = { navController.popBackStack(Destinations.CREAR_OT_PASO3_1, inclusive = false) },
                    onSiguiente = { navController.navigate(Destinations.CREAR_OT_PASO5) },
                    onCerrar = { navController.navigate(Destinations.OTS) }
                )
            }
            composable(Destinations.CREAR_OT_PASO5) {
                CrearOTPaso5Screen(
                    onAtras = { navController.popBackStack(Destinations.CREAR_OT_PASO4, inclusive = false) },
                    onFinalizar = { navController.navigate(Destinations.OTS) },
                    onCerrar = { navController.navigate(Destinations.OTS) }
                )
            }
        }
    }
}
