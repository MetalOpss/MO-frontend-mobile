package com.example.metalops.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Task(
    val time: String,
    val name: String,
    val description: String,
    val status: String = "Pendiente"
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf("Pendientes") }

    val tasks = listOf(
        Task("12:59 PM", "Nombre de la tarea", "Descripción de la tarea..."),
        Task("12:59 PM", "Nombre de la tarea", "Descripción de la tarea..."),
        Task("12:59 PM", "Nombre de la tarea", "Descripción de la tarea...")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp)
    ) {
        // Header
        WelcomeHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Cards
        StatsSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Tasks Section
        TasksSection(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            tasks = tasks
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WelcomeHeader() {
    val currentDate = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM")

    Column {
        Text(
            text = "¡Bienvenido, <Fullname>!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = dateFormatter.format(currentDate),
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tareas hoy card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF366A9A))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tareas hoy",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "15",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Urgentes card
        Card(
            modifier = Modifier.weight(0.6f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Urgentes",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "3",
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TasksSection(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    tasks: List<Task>
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tareas de hoy:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Text(
                text = "Jueves, 4 de septiembre",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(
                    text = "Pendientes",
                    isSelected = selectedTab == "Pendientes",
                    onClick = { onTabSelected("Pendientes") }
                )
                TabButton(
                    text = "Completadas",
                    isSelected = selectedTab == "Completadas",
                    onClick = { onTabSelected("Completadas") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(task = task)
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF366A9A) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${task.time}, \"${task.name}\" -> \"${task.description}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Text(
                        text = task.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            IconButton(onClick = { /* Handle more options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                    tint = Color.Gray
                )
            }
        }
    }
}