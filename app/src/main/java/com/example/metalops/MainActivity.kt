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
            composable("ots") { OTScreen() }
            composable("registrar_cliente") { RegistrarClienteScreen(
                onClose = { navController.popBackStack() },
                onAction = { navController.popBackStack() }
            ) }
            composable("editar_cliente") { EditarClienteScreen(
                onClose = { navController.popBackStack() },
                onAction = { navController.popBackStack() }
            ) }
        }
    }
}
