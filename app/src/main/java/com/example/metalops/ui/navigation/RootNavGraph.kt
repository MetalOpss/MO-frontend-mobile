package com.example.metalops.ui.navigation

import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.os.Build
import com.example.metalops.ui.agente.navigation.AppNavGraph
import com.example.metalops.ui.planner.navigation.PlannerNavGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "agente" // o "planner"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("agente") { AppNavGraph() }
        composable("planner") { PlannerNavGraph() }
    }
}
