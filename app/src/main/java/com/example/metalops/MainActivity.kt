package com.example.metalops

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<< HEAD
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metalops.ui.BottomBar
=======
import androidx.annotation.RequiresApi
>>>>>>> 6d82b9db836fbcc6314e86caee63b05c24b885fb
import com.example.metalops.ui.HomeScreen
import com.example.metalops.ui.ClientesScreen
import com.example.metalops.ui.OTScreen
import com.example.metalops.ui.theme.MetalOpsTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetalOpsTheme {
<<<<<<< HEAD
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
=======
                HomeScreen() // Solo la pantalla de inicio
>>>>>>> 6d82b9db836fbcc6314e86caee63b05c24b885fb
            }
        }
    }
}
