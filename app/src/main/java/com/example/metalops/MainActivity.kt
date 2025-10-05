package com.example.metalops

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.ui.*
import com.example.metalops.ui.components.BottomBar
import com.example.metalops.ui.theme.MetalOpsTheme
import com.example.metalops.ui.components.BottomBar
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetalOpsTheme {
                MainScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 🏠 Pantalla principal
            composable("home") { HomeScreen(navController) }

            // 👥 Clientes
            composable("clientes") { ClientesScreen(navController) }

            // 🧾 Órdenes de trabajo
            composable("ots") { OrdenesTrabajoScreen(navController) }

            // 🆕 Registrar / Editar Cliente
            composable("registrar_cliente") {
                RegistrarClienteScreen(
                    onClose = { navController.popBackStack() },
                    onAction = { navController.popBackStack() }
                )
            }
            composable("editar_cliente") {
                EditarClienteScreen(
                    onClose = { navController.popBackStack() },
                    onAction = { navController.popBackStack() }
                )
            }

            // RUTAS DEL FAB (Perfil / Notificaciones / Configuración)
            composable("perfil") { PerfilScreen(navController) }
            composable("notificaciones") { NotificacionesScreen(navController) }
            composable("configuracion") { ConfiguracionScreen(navController) }

            // Pantallas de Info / Contacto (usadas desde Configuración)
            composable("info_build") { InfoBuildScreen(navController) }
            composable("info_team")  { InfoTeamScreen(navController) }
            composable("contacto")   { ContactoScreen(navController) }

            // 🧩 Flujo de creación de OT
            composable("crear_ot_paso1") {
                CrearOTPaso1Screen(
                    onSiguiente = { navController.navigate("crear_ot_paso2") },
                    onCerrar = { navController.navigate("ots") }
                )
            }
            composable("crear_ot_paso2") {
                CrearOTPaso2Screen(
                    onSiguiente = { navController.navigate("crear_ot_paso3") },
                    onAtras = { navController.popBackStack() },
                    onCerrar = { navController.navigate("ots") }
                )
            }
            composable("crear_ot_paso3") {
                CrearOTPaso3Screen(
                    onSiguiente = { navController.navigate("crear_ot_paso3_1") },
                    onAtras = { navController.popBackStack() },
                    onCerrar = { navController.navigate("ots") }
                )
            }
            composable("crear_ot_paso3_1") {
                CrearOTPaso3_1Screen(
                    onAtras = { navController.popBackStack("crear_ot_paso3", inclusive = false) },
                    onSiguiente = { navController.navigate("crear_ot_paso4") },
                    onCerrar = { navController.navigate("ots") }
                )
            }
            composable("crear_ot_paso4") {
                CrearOTPaso4Screen(
                    onAtras = { navController.popBackStack("crear_ot_paso3_1", inclusive = false) },
                    onSiguiente = { navController.navigate("crear_ot_paso5") },
                    onCerrar = { navController.navigate("ots") }
                )
            }
            composable("crear_ot_paso5") {
                CrearOTPaso5Screen(
                    onAtras = { navController.popBackStack("crear_ot_paso4", inclusive = false) },
                    onFinalizar = { navController.navigate("ots") },
                    onCerrar = { navController.navigate("ots") }
                )
            }
        }
    }
}
