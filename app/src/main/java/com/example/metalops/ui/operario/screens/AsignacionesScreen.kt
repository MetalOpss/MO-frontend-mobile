package com.example.metalops.ui.operario.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.model.WorkOrder
import com.example.metalops.data.remote.WorkOrdersRepository
import kotlinx.coroutines.launch

@Composable
fun AsignacionesScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repo = remember { WorkOrdersRepository() }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var workOrders by remember { mutableStateOf<List<WorkOrder>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loadAssignments(
            repo = repo,
            onResult = { workOrders = it },
            onError = { errorMessage = it },
            onLoadingChange = { isLoading = it }
        )
    }

    val primaryBlue = Color(0xFF1976D2)
    val grayBackground = Color(0xFFF5F5F5)

    val withOperator = workOrders.count { !it.assignedOperator.isNullOrBlank() }
    val withoutOperator = workOrders.size - withOperator

    val filteredOrders by remember(workOrders, searchQuery) {
        mutableStateOf(
            workOrders
                .filter { wo ->
                    if (searchQuery.isBlank()) true
                    else {
                        wo.title.contains(searchQuery, true) ||
                                wo.clientName.contains(searchQuery, true) ||
                                wo.location.contains(searchQuery, true)
                    }
                }
                .sortedByDescending { it.scheduledDate + " " + it.scheduledTime }
        )
    }

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
                text = "Asignaciones",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Vista rápida de qué operarios tienen órdenes asignadas.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Resumen ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryBox(
                    title = "OT con operario",
                    value = withOperator.toString(),
                    highlightColor = Color(0xFFE3F2FD),
                    textColor = primaryBlue,
                    modifier = Modifier.weight(1f)
                )
                SummaryBox(
                    title = "OT sin operario",
                    value = withoutOperator.toString(),
                    highlightColor = Color(0xFFFFEBEE),
                    textColor = Color(0xFFD32F2F),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por OT, cliente u operario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Toca para reintentar.",
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                loadAssignments(
                                    repo = repo,
                                    onResult = { workOrders = it },
                                    onError = { errorMessage = it },
                                    onLoadingChange = { isLoading = it }
                                )
                            }
                        },
                    color = primaryBlue,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (filteredOrders.isEmpty() && !isLoading && errorMessage == null) {
                Text(
                    text = "No hay órdenes asignadas.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredOrders) { wo ->
                        AsignacionCard(
                            workOrder = wo,
                            primaryBlue = primaryBlue
                        )
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

private suspend fun loadAssignments(
    repo: WorkOrdersRepository,
    onResult: (List<WorkOrder>) -> Unit,
    onError: (String?) -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    onError(null)
    try {
        val result = repo.getWorkOrders()
        onResult(result)
    } catch (e: Exception) {
        e.printStackTrace()
        onError(e.message ?: "Error al obtener órdenes")
    } finally {
        onLoadingChange(false)
    }
}

/* ---------- UI helpers ---------- */

@Composable
private fun SummaryBox(
    title: String,
    value: String,
    highlightColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
            Box(
                modifier = Modifier
                    .background(highlightColor, shape = CardDefaults.shape)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun AsignacionCard(
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
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Cliente: ${workOrder.clientName}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Operario asignado: ${workOrder.assignedOperator ?: "Sin asignar"}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            EstadoBadge(status = workOrder.status, primaryBlue = primaryBlue)
        }
    }
}

@Composable
private fun EstadoBadge(
    status: String,
    primaryBlue: Color
) {
    val (bg, fg) = when {
        status.equals("en registro", true) ->
            Color(0xFFE3F2FD) to primaryBlue
        status.equals("en progreso", true) || status.equals("en ejecución", true) ->
            Color(0xFFFFF3E0) to Color(0xFFF57C00)
        status.equals("por corregir", true) ->
            Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        status.equals("completada", true) ->
            Color(0xFFE8F5E9) to Color(0xFF388E3C)
        else ->
            Color(0xFFE0E0E0) to Color(0xFF424242)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = CardDefaults.shape
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = fg,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}