package com.example.metalops.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * BottomBar reutilizable.
 *
 * @param navController el NavController del grafo actual (agente o planner)
 * @param items lista de pestañas a renderizar (cada una tiene route/label/icon)
 * @param startRoute ruta raíz/base de ese grafo, usada en popUpTo para limpiar backstack
 */
@Composable
fun BottomBar(
    navController: NavController,
    items: List<BottomNavItem>,
    startRoute: String
) {
    NavigationBar(
        containerColor = Color(0xFFF8F8F8),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // vamos al tab seleccionado
                            launchSingleTop = true
                            // limpiamos stack hasta la raíz del grafo de ese rol
                            popUpTo(startRoute) {
                                inclusive = false
                            }
                        }
                    }
                },
                icon = {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF295FAB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color.White
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = Color.Gray
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color(0xFF295FAB) else Color.Gray
                    )
                }
            )
        }
    }
}
