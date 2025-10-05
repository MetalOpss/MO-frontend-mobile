package com.example.metalops.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // ejemplo de datos (reemplaza por tu fuente real)
    val sample = remember {
        mutableStateListOf(
            NotificationModel(1, "Notificación 1", "Cuerpo", read = false),
            NotificationModel(2, "Notificación 2", "Cuerpo", read = true),
            NotificationModel(3, "Notificación 3", "Cuerpo", read = false),
            NotificationModel(4, "Notificación 4", "Cuerpo", read = true),
            NotificationModel(5, "Notificación 5", "Cuerpo", read = false),
            NotificationModel(6, "Notificación 6", "Cuerpo", read = false),
        )
    }

    var selectedTab by remember { mutableStateOf(TabSelection.UNREAD) } // default
    val topPadding = 8.dp

    Scaffold(
        topBar = {
            NotificationHeader(navController = navController, onClose = { navController.popBackStack() })
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Spacer(modifier = Modifier.height(topPadding))

            NotificationTabs(selected = selectedTab, onSelect = { selectedTab = it })

            // List
            NotificationList(
                initialItems = sample,
                filter = selectedTab,
                onToggleRead = { id ->
                    // callback cuando se marca/ desmarca
                    val idx = sample.indexOfFirst { it.id == id }
                    if (idx >= 0) sample[idx] = sample[idx].copy(read = !sample[idx].read)
                    println("toggle read: $id")
                },
                onOpen = { id ->
                    println("open notification: $id")
                    // navega a detalle si tienes ruta: navController.navigate("notificacion_detalle/$id")
                }
            )
        }
    }
}
