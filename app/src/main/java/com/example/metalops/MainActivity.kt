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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.ui.*
import com.example.metalops.ui.theme.MetalOpsTheme

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

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("clientes") { ClientesScreen(navController) }
            composable("ots") { OrdenesTrabajoScreen(navController) }

            // Crear OT Paso 1
            composable("crear_ot_paso1") {
                CrearOTPaso1Screen(
                    onSiguiente = { navController.navigate("crear_ot_paso2") },
                    onCerrar = { navController.navigate("ots") }
                )
            }

            // Crear OT Paso 2
            composable("crear_ot_paso2") {
                CrearOTPaso2Screen(
                    onSiguiente = { navController.navigate("crear_ot_paso3") },
                    onAtras = { navController.popBackStack() },
                    onCerrar = { navController.navigate("ots") }
                )
            }

            // Crear OT Paso 3
            composable("crear_ot_paso3") {
                CrearOTPaso3Screen(
                    onSiguiente = { navController.navigate("crear_ot_paso3_1") }, // va a 3_1
                    onAtras = { navController.popBackStack() },
                    onCerrar = { navController.popBackStack("ots", inclusive = false) }
                )
            }

            // Crear OT Paso 3_1 (antes paso4) - llamada consistente con la firma del Composable
            composable("crear_ot_paso3_1") {
                CrearOTPaso3_1Screen(
                    onAtras = { navController.popBackStack("crear_ot_paso3", inclusive = false) },
                    onSiguiente = { /* navController.navigate("crear_ot_paso5") o confirmar */ },
                    onCerrar = { navController.popBackStack("ots", inclusive = false) }
                )
            }

            // Registrar / Editar cliente
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
        }
    }
}

