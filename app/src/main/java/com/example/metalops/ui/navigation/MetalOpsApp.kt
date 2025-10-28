package com.example.metalops.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MetalOpsApp() {
    val navController = rememberNavController()

    // 🚀 Punto de entrada de la aplicación
    // Ahora siempre empieza con autenticación
    RootNavGraph(navController = navController)
}