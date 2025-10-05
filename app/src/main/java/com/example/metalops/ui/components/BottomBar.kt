package com.example.metalops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
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

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", "Inicio", Icons.Default.Home),
        BottomNavItem("clientes", "Clientes", Icons.Default.People),
        BottomNavItem("ots", "OT's", Icons.Default.List)
    )

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
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(40.dp) // tamaño fijo de la bolita
                                .background(Color(0xFF295FAB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = Color.White
                            )
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label, tint = Color.Gray)
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
