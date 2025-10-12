package com.example.metalops.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MetalOpsApp() {
    // 🟢 Cambia aquí según lo que quieras probar
    val isPlanner = false // true = planner, false = agente

    if (isPlanner) {
        com.example.metalops.ui.planner.navigation.PlannerNavGraph()
    } else {
        com.example.metalops.ui.agente.navigation.AppNavGraph()
    }
}