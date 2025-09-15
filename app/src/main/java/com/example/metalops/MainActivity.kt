package com.example.metalops

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.ui.BottomBar
import com.example.metalops.ui.HomeScreen
import com.example.metalops.ui.ClientesScreen
import com.example.metalops.ui.OTScreen
import com.example.metalops.ui.theme.MetalOpsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetalOpsTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomBar(navController) } // Barra de navegación inferior
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen() }
                        composable("clientes") { ClientesScreen() }
                        composable("ots") { OTScreen() }
                    }
                }
            }
        }
    }
}
