package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.ui.components.*

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Pendientes") }

    val tasks = listOf(
        Task("12:59 PM", "Nombre de la tarea", "Descripción de la tarea..."),
        Task("12:59 PM", "Nombre de la tarea", "Descripción de la tarea..."),
        Task("12:59 PM", "Nombre de la tarea", "Descripción de la tarea...")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            WelcomeHeader()

            Spacer(modifier = Modifier.height(24.dp))

            StatsSection()

            Spacer(modifier = Modifier.height(24.dp))

            TasksSection(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                tasks = tasks
            )
        }

        // ----------------- FAB menú flotante (rutas corregidas) -----------------
        FabMenu(
            modifier = Modifier.align(Alignment.BottomEnd),
            onNotificacionesClick = {
                println("CLICK: notificaciones (HomeScreen lambda)")
                navController.navigate("notificaciones")
            },
            onPerfilClick = {
                println("CLICK: perfil (HomeScreen lambda)")
                navController.navigate("perfil")
            },
            onConfiguracionClick = {
                println("CLICK: configuracion (HomeScreen lambda)")
                navController.navigate("configuracion")
            }
        )
    }
}
