package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.metalops.ui.components.*

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
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

        // ----------------- FAB flotante -----------------
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { expanded = !expanded },
                shape = CircleShape,
                containerColor = Color(0xFF366A9A)
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.White)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Opción 1") },
                    onClick = { expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Opción 2") },
                    onClick = { expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Opción 3") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}