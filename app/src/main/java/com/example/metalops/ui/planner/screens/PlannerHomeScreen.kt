package com.example.metalops.ui.planner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.session.SessionManager
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.remote.PlannerDashboardRepository
import com.example.metalops.data.remote.PlannerWorkOrder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PlannerHomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repo = remember { PlannerDashboardRepository() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val auth = remember { FirebaseAuth.getInstance() }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var workOrders by remember { mutableStateOf<List<PlannerWorkOrder>>(emptyList()) }

    var selectedStatus by remember { mutableStateOf("todos") }
    var selectedWorkOrder by remember { mutableStateOf<PlannerWorkOrder?>(null) }

    var userName by remember { mutableStateOf<String?>(null) }

    // Nombre/correo del usuario
    LaunchedEffect(Unit) {
        userName = sessionManager.getUserName()
            ?: auth.currentUser?.email
    }

    // Cargar OT
    LaunchedEffect(Unit) {
        loadPlannerData(
            repository = repo,
            onResult = { workOrders = it },
            onError = { errorMessage = it },
            onLoadingChange = { isLoading = it }
        )
    }

    // Filtro
    val filteredWorkOrders: List<PlannerWorkOrder> = when (selectedStatus) {
        "en registro" -> workOrders.filter { it.status.equals("en registro", true) }
        "en progreso" -> workOrders.filter { it.status.equals("en progreso", true) }
        "por corregir" -> workOrders.filter { it.status.equals("por corregir", true) }
        "en ejecución" -> workOrders.filter { it.status.equals("en ejecución", true) }
        else -> workOrders
    }

    val primaryBlue = Color(0xFF1976D2)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                AppHeader()

                Spacer(modifier = Modifier.height(8.dp))

                // Bienvenida
                Text(
                    text = "Bienvenido,",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.DarkGray
                )
                Text(
                    text = userName?.takeIf { it.isNotBlank() } ?: "Usuario planner",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats
                PlannerStatsRow(workOrders = workOrders)

                Spacer(modifier = Modifier.height(12.dp))

                // Producción semanal
                ProductionWeeklyCard(
                    workOrders = workOrders,
                    primaryBlue = primaryBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Error
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = {
                        scope.launch {
                            loadPlannerData(
                                repository = repo,
                                onResult = { workOrders = it },
                                onError = { errorMessage = it },
                                onLoadingChange = { isLoading = it }
                            )
                        }
                    }) {
                        Text("Reintentar")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Título + filtros
                Text(
                    text = "Órdenes de trabajo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusFilterRow(
                    selected = selectedStatus,
                    onSelectedChange = { selectedStatus = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // LISTA SIN ESPACIO VACÍO (scroll vertical)
                val listScroll = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)              // ocupa todo el resto de pantalla
                        .verticalScroll(listScroll),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredWorkOrders.isEmpty() && !isLoading && errorMessage == null) {
                        Text(
                            text = "No hay órdenes para este filtro.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        filteredWorkOrders.forEach { ot ->
                            WorkOrderCard(
                                workOrder = ot,
                                onClick = { selectedWorkOrder = ot }
                            )
                        }
                    }
                }
            }

            // Loader
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

            // Detalle
            selectedWorkOrder?.let { ot ->
                WorkOrderDetailDialog(
                    workOrder = ot,
                    onDismiss = { selectedWorkOrder = null }
                )
            }
        }
    }
}

/* ----------- LÓGICA DE CARGA ------------ */

private suspend fun loadPlannerData(
    repository: PlannerDashboardRepository,
    onResult: (List<PlannerWorkOrder>) -> Unit,
    onError: (String?) -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    onError(null)

    try {
        val data = repository.getAllWorkOrders()
        onResult(data)
    } catch (e: Exception) {
        e.printStackTrace()
        onError(e.message ?: "Error al cargar las órdenes de trabajo")
    } finally {
        onLoadingChange(false)
    }
}

/* ----------- STATS ------------ */

@Composable
private fun PlannerStatsRow(
    workOrders: List<PlannerWorkOrder>
) {
    val total = workOrders.size
    val registro = workOrders.count { it.status.equals("en registro", true) }
    val progreso = workOrders.count { it.status.equals("en progreso", true) }
    val corregir = workOrders.count { it.status.equals("por corregir", true) }
    val ejecucion = workOrders.count { it.status.equals("en ejecución", true) }
    val urgentes = workOrders.count { it.isUrgent }

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCardInline(
            title = "OT totales",
            value = total.toString(),
            containerColor = Color(0xFF1976D2),
            titleColor = Color.White,
            valueColor = Color.White
        )
        StatCardInline("En registro", registro.toString())
        StatCardInline("En progreso", progreso.toString())
        StatCardInline("Por corregir", corregir.toString())
        StatCardInline("En ejecución", ejecucion.toString())
        StatCardInline("Urgentes", urgentes.toString())
    }
}

@Composable
private fun StatCardInline(
    title: String,
    value: String,
    containerColor: Color = Color.White,
    titleColor: Color = Color.Gray,
    valueColor: Color = Color.Black
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(80.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = titleColor
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

/* ----------- PRODUCCIÓN SEMANAL ------------ */

@Composable
private fun ProductionWeeklyCard(
    workOrders: List<PlannerWorkOrder>,
    primaryBlue: Color
) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val countsPerDay = days.map { date ->
        val key = date.format(formatter)
        workOrders.count { it.scheduledDate == key }
    }

    val labels = listOf("S", "M", "T", "W", "T", "F", "S")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Producción semanal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "OT programadas por día (últimos 7 días)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxCount = (countsPerDay.maxOrNull() ?: 1).coerceAtLeast(1)
                countsPerDay.forEach { value ->
                    val ratio = if (maxCount == 0) 0f else value.toFloat() / maxCount.toFloat()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .width(10.dp)
                                .height(60.dp * ratio)
                                .clip(RoundedCornerShape(3.dp))
                                .background(primaryBlue)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEach { lbl ->
                    Text(
                        text = lbl,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/* ----------- FILTROS (SCROLL HORIZONTAL) ------------ */

@Composable
private fun StatusFilterRow(
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    val options = listOf(
        "todos" to "Todos",
        "en registro" to "En registro",
        "en progreso" to "En progreso",
        "por corregir" to "Por corregir",
        "en ejecución" to "En ejecución"
    )

    val scroll = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { (value, label) ->
            val isSelected = selected == value
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) Color(0xFF1976D2) else Color(0xFFE0E0E0)
                    )
                    .clickable { onSelectedChange(value) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.White else Color.DarkGray
                )
            }
        }
    }
}

/* ----------- CARD OT Y DETALLE ------------ */

@Composable
private fun WorkOrderCard(
    workOrder: PlannerWorkOrder,
    onClick: () -> Unit
) {
    val urgentColor = if (workOrder.isUrgent) Color(0xFFD32F2F) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workOrder.code,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (workOrder.isUrgent) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(urgentColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Urgente",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = workOrder.scheduledDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = workOrder.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = workOrder.client,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            val statusLabel = workOrder.status.replaceFirstChar { it.uppercaseChar() }

            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF424242)
            )
        }
    }
}

@Composable
private fun WorkOrderDetailDialog(
    workOrder: PlannerWorkOrder,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = workOrder.code,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = workOrder.title,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cliente: ${workOrder.client}")
                Text("Estado: ${workOrder.status}")
                Text("Fecha programada: ${workOrder.scheduledDate}")
                if (workOrder.isUrgent) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Esta orden está marcada como URGENTE.",
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}