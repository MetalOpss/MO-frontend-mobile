package com.example.metalops.ui.operario.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.model.WorkOrder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun TurnosOperarioScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val db = remember { FirebaseFirestore.getInstance() }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var workOrders by remember { mutableStateOf<List<WorkOrder>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null

            val snapshot = db.collection("work-orders")
                .get()
                .await()

            val list = snapshot.documents.map { doc ->
                WorkOrder(
                    id = doc.id,
                    code = doc.getString("code") ?: doc.id.take(8).uppercase(),
                    title = doc.getString("title") ?: "",
                    clientName = doc.getString("clientName") ?: "Sin cliente",
                    location = doc.getString("location") ?: "Sin ubicación",
                    priority = doc.getString("priority") ?: "media",
                    status = doc.getString("status") ?: "en registro",
                    type = doc.getString("type") ?: "normal",
                    scheduledDate = doc.getString("scheduledDate") ?: "",
                    scheduledTime = doc.getString("scheduledTime") ?: "",
                    isUrgent = doc.getBoolean("isUrgent") ?: false,
                    correctionOf = doc.getString("correctionOf"),
                    plannerId = doc.getString("plannerId"),
                    errorFlag = doc.getBoolean("errorFlag") ?: false,
                    errorMessage = doc.getString("errorMessage"),
                    designFileUrl = doc.getString("designFileUrl"),
                    assignedOperator = doc.getString("assignedOperator"),
                    assignedMachine = doc.getString("assignedMachine"),
                    plannedStartTime = doc.getString("plannedStartTime"),
                    plannedEndTime = doc.getString("plannedEndTime")
                )
            }

            workOrders = list
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = e.message ?: "Error al cargar tus turnos."
        } finally {
            isLoading = false
        }
    }

    val primaryBlue = Color(0xFF1976D2)
    val grayBackground = Color(0xFFF5F5F5)

    val totalTurnos = workOrders.size
    val completados = workOrders.count { it.status.equals("completada", true) }
    val pendientes = workOrders.count { !it.status.equals("completada", true) }

    val turnoActual: WorkOrder? = workOrders
        .filter {
            it.status.equals("en ejecución", true) ||
                    it.status.equals("en progreso", true)
        }
        .maxByOrNull { it.scheduledDate + " " + it.scheduledTime }

    val sortedTurnos = workOrders
        .sortedByDescending { it.scheduledDate + " " + it.scheduledTime }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(grayBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppHeader()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Turnos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Consulta tus turnos programados y en curso.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryTurnoCard(
                    title = "Turno actual",
                    value = turnoActual?.title ?: "Sin turno",
                    highlighted = true,
                    modifier = Modifier.weight(1f)
                )
                SummaryTurnoCard(
                    title = "Pendientes",
                    value = pendientes.toString(),
                    highlighted = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryTurnoCard(
                    title = "Completados",
                    value = completados.toString(),
                    highlighted = false,
                    modifier = Modifier.weight(1f)
                )
                SummaryTurnoCard(
                    title = "Total turnos",
                    value = totalTurnos.toString(),
                    highlighted = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Turno actual",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (turnoActual == null) {
                Text(
                    text = "Ahora mismo no tienes un turno en curso.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            } else {
                TurnoDetalleCard(turnoActual, primaryBlue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Próximos y recientes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (sortedTurnos.isEmpty() && !isLoading && errorMessage == null) {
                Text(
                    text = "Aún no tienes turnos registrados.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sortedTurnos) { wo ->
                        TurnoItemCard(wo, primaryBlue)
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

/* ---- UI helpers ---- */

@Composable
private fun SummaryTurnoCard(
    title: String,
    value: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryBlue = Color(0xFF1976D2)
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) primaryBlue else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (highlighted) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun TurnoDetalleCard(
    workOrder: WorkOrder,
    primaryBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = workOrder.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cliente: ${workOrder.clientName}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Programado: ${workOrder.scheduledDate} ${workOrder.scheduledTime}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estado: ${workOrder.status.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall.copy(color = primaryBlue)
            )
        }
    }
}

@Composable
private fun TurnoItemCard(
    workOrder: WorkOrder,
    primaryBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "OT · ${workOrder.title}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Cliente: ${workOrder.clientName}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Programado: ${workOrder.scheduledDate} ${workOrder.scheduledTime}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estado: ${workOrder.status.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.labelSmall.copy(color = primaryBlue)
            )
        }
    }
}
