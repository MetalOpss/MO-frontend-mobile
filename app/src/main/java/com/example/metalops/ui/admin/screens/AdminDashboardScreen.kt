package com.example.metalops.ui.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.model.WorkOrder
import com.example.metalops.data.remote.WorkOrdersRepository

@Composable
fun AdminDashboardScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repo = remember { WorkOrdersRepository() }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var workOrders by remember { mutableStateOf<List<WorkOrder>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            workOrders = repo.getWorkOrders() // Admin ve TODAS las OT
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = e.message ?: "Error al cargar información"
        } finally {
            isLoading = false
        }
    }

    val primaryBlue = Color(0xFF1976D2)

    val total = workOrders.size
    val enRegistro = workOrders.count { it.status.equals("en registro", true) }
    val enProgreso = workOrders.count {
        it.status.equals("en progreso", true) || it.status.equals("en ejecución", true)
    }
    val porCorregir = workOrders.count {
        it.status.equals("por corregir", true) || it.type.equals("correccion", true)
    }
    val completadas = workOrders.count { it.status.equals("completada", true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppHeader()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Panel general (Admin)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Resumen global de órdenes y operación.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -------- Resumen de OT --------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminSummaryCard(
                    title = "Total OT",
                    value = total.toString(),
                    highlighted = true,
                    modifier = Modifier.weight(1f)
                )
                AdminSummaryCard(
                    title = "En progreso",
                    value = enProgreso.toString(),
                    highlighted = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminSummaryCard(
                    title = "Por corregir",
                    value = porCorregir.toString(),
                    highlighted = false,
                    modifier = Modifier.weight(1f)
                )
                AdminSummaryCard(
                    title = "Completadas",
                    value = completadas.toString(),
                    highlighted = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Últimas órdenes registradas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (workOrders.isEmpty() && !isLoading && errorMessage == null) {
                Text(
                    text = "Aún no hay órdenes registradas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(workOrders.take(10)) { wo ->
                        AdminWorkOrderRow(workOrder = wo, primaryBlue = primaryBlue)
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x33000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun AdminSummaryCard(
    title: String,
    value: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val blue = Color(0xFF1976D2)
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) blue else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (highlighted) Color.White else Color.Gray
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = if (highlighted) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun AdminWorkOrderRow(
    workOrder: WorkOrder,
    primaryBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "OT · ${workOrder.title}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "Cliente: ${workOrder.clientName}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
            Text(
                text = "Estado: ${workOrder.status}",
                style = MaterialTheme.typography.labelSmall.copy(color = primaryBlue)
            )
        }
    }
}