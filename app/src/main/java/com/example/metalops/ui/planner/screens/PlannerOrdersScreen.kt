package com.example.metalops.ui.planner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.model.WorkOrder
import com.example.metalops.data.remote.WorkOrdersRepository
import com.example.metalops.ui.agente.theme.MetalOpsTheme
import kotlinx.coroutines.launch

@Composable
fun PlannerOrdersScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repository = remember { WorkOrdersRepository() }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var workOrders by remember { mutableStateOf<List<WorkOrder>>(emptyList()) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Cargar OTs
    LaunchedEffect(Unit) {
        loadWorkOrdersPlanner(
            repository = repository,
            onResult = { workOrders = it },
            onError = { errorMessage = it },
            onLoadingChange = { isLoading = it }
        )
    }

    // Totales para tarjetas
    val total = workOrders.size
    val inCourse = workOrders.count {
        it.status.equals("en progreso", true) || it.status.equals("en ejecución", true)
    }
    val corrections = workOrders.count { it.type.equals("correccion", true) }
    val completed = workOrders.count { it.status.equals("completada", true) }

    // Filtro + búsqueda
    val filteredOrders by remember(workOrders, searchQuery, selectedFilter) {
        mutableStateOf(
            workOrders
                .filter { wo ->
                    when (selectedFilter) {
                        "En registro" -> wo.status.equals("en registro", true)
                        "En progreso" -> wo.status.equals("en progreso", true)
                        "En ejecución" -> wo.status.equals("en ejecución", true)
                        "Por corregir" -> wo.status.equals("por corregir", true) ||
                                wo.type.equals("correccion", true)
                        "Completadas" -> wo.status.equals("completada", true)
                        else -> true
                    }
                }
                .filter { wo ->
                    if (searchQuery.isBlank()) true
                    else {
                        wo.title.contains(searchQuery, true) ||
                                wo.clientName.contains(searchQuery, true) ||
                                wo.location.contains(searchQuery, true)
                    }
                }
        )
    }

    val primaryBlue = Color(0xFF1976D2)

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
                text = "Órdenes de trabajo",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Visualiza y prioriza la carga de OT.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -------- Resumen de producción --------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Totales",
                    value = total.toString(),
                    highlighted = true,
                    icon = Icons.Default.Assignment,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "En curso",
                    value = inCourse.toString(),
                    highlighted = false,
                    icon = Icons.Default.PlayArrow,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Corrección",
                    value = corrections.toString(),
                    highlighted = false,
                    icon = Icons.Default.Build,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Completadas",
                    value = completed.toString(),
                    highlighted = false,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------- Búsqueda --------
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar OT por título, cliente o ubicación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // -------- Chips de filtro (scroll horizontal) --------
            PlannerFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                primaryBlue = primaryBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Listado de órdenes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            loadWorkOrdersPlanner(
                                repository = repository,
                                onResult = { workOrders = it },
                                onError = { errorMessage = it },
                                onLoadingChange = { isLoading = it }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    )
                ) {
                    Text("Reintentar")
                }
            }

            if (filteredOrders.isEmpty() && !isLoading && errorMessage == null) {
                Text(
                    text = "No hay órdenes que coincidan con el filtro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredOrders) { wo ->
                        PlannerOrderCard(
                            workOrder = wo,
                            primaryBlue = primaryBlue,
                            onClick = {
                                // 👇 Navegamos al detalle con el id de la OT
                                navController.navigate("planner_order_detail/${wo.id}")
                            }
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

/* -------------------- COMPONENTES UI -------------------- */

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    highlighted: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val blue = Color(0xFF1976D2)
    Card(
        modifier = modifier
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) blue else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
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
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (highlighted) Color.White else blue
            )
        }
    }
}

@Composable
private fun PlannerFilterRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    primaryBlue: Color
) {
    val filters = listOf(
        "Todos",
        "En registro",
        "En progreso",
        "En ejecución",
        "Por corregir",
        "Completadas"
    )

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { f ->
            PlannerFilterChip(
                text = f,
                isSelected = selectedFilter == f,
                primaryBlue = primaryBlue,
                onClick = { onFilterSelected(f) }
            )
        }
    }
}

@Composable
private fun PlannerFilterChip(
    text: String,
    isSelected: Boolean,
    primaryBlue: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .background(
                color = if (isSelected) primaryBlue else Color(0xFFE0E0E0),
                shape = CardDefaults.shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun PlannerOrderCard(
    workOrder: WorkOrder,
    primaryBlue: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Título + estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = workOrder.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Cliente: ${workOrder.clientName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                StatusBadge(status = workOrder.status, primaryBlue = primaryBlue)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ubicación: ${workOrder.location}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityBadge(priority = workOrder.priority)
                Text(
                    text = "Programada: ${workOrder.scheduledDate}" +
                            if (workOrder.scheduledTime.isNotBlank())
                                " • ${workOrder.scheduledTime}"
                            else "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
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

    Box(
        modifier = Modifier
            .background(bg, shape = CardDefaults.shape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall.copy(
                color = fg,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val (bg, fg) = when (priority.lowercase()) {
        "alta" -> Color(0xFFFFE0E0) to Color(0xFFD32F2F)
        "media" -> Color(0xFFFFF3E0) to Color(0xFFF57C00)
        "baja" -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
        else -> Color(0xFFE0E0E0) to Color(0xFF424242)
    }

    Box(
        modifier = Modifier
            .background(bg, shape = CardDefaults.shape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Prioridad: $priority",
            style = MaterialTheme.typography.labelSmall.copy(
                color = fg,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/* -------------------- CARGA DE DATOS -------------------- */

private suspend fun loadWorkOrdersPlanner(
    repository: WorkOrdersRepository,
    onResult: (List<WorkOrder>) -> Unit,
    onError: (String?) -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    onError(null)
    try {
        val result = repository.getWorkOrders()
        onResult(result)
    } catch (e: Exception) {
        e.printStackTrace()
        onError(e.message ?: "Error al obtener órdenes")
    } finally {
        onLoadingChange(false)
    }
}

/* -------------------- PREVIEW -------------------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PlannerOrdersScreenPreview() {
    MetalOpsTheme {
        PlannerOrdersScreen(
            navController = rememberNavController()
        )
    }
}