package com.example.metalops.ui.planner.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.model.WorkOrder
import com.example.metalops.data.remote.OperarioBasic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun PlannerOrderDetailScreen(
    navController: NavHostController,
    workOrderId: String,
    workOrderTitle: String,
    clientName: String
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val context = LocalContext.current

    var workOrder by remember { mutableStateOf<WorkOrder?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar OT desde Firestore
    LaunchedEffect(workOrderId) {
        isLoading = true
        errorMessage = null
        try {
            val snap = db.collection("work-orders")
                .document(workOrderId)
                .get()
                .await()

            if (snap.exists()) {
                val wo = WorkOrder(
                    id = snap.id,
                    code = snap.getString("code") ?: snap.id.takeLast(6).uppercase(),
                    title = snap.getString("title") ?: workOrderTitle,
                    clientName = snap.getString("clientName") ?: clientName,
                    location = snap.getString("location") ?: "Sin ubicación",
                    priority = snap.getString("priority") ?: "media",
                    status = snap.getString("status") ?: "en registro",
                    type = snap.getString("type") ?: "normal",
                    scheduledDate = snap.getString("scheduledDate") ?: "",
                    scheduledTime = snap.getString("scheduledTime") ?: "",
                    isUrgent = snap.getBoolean("isUrgent") ?: false,
                    correctionOf = snap.getString("correctionOf"),
                    plannerId = snap.getString("plannerId"),
                    errorFlag = snap.getBoolean("errorFlag") ?: false,
                    errorMessage = snap.getString("errorMessage"),
                    designFileUrl = snap.getString("designFileUrl"),
                    // campos de planificación (pueden no existir todavía)
                    assignedOperator = snap.getString("assignedOperator"),
                    assignedMachine = snap.getString("assignedMachine"),
                    plannedStartTime = snap.getString("plannedStartTime"),
                    plannedEndTime = snap.getString("plannedEndTime")
                )
                workOrder = wo
            } else {
                errorMessage = "No se encontró la OT"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = e.message ?: "Error al cargar OT"
        } finally {
            isLoading = false
        }
    }

    val primaryBlue = Color(0xFF1976D2)

    Box(
        modifier = Modifier
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
                text = "Detalle de OT",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Revisa la información antes de planificar recursos.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red
                )
            }

            workOrder?.let { wo ->

                // -------- Card con info general --------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = wo.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cliente: ${wo.clientName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Ubicación: ${wo.location}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Estado",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray
                                    )
                                )
                                Text(
                                    text = wo.status,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Prioridad",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray
                                    )
                                )
                                Text(
                                    text = wo.priority,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Programada: ${wo.scheduledDate}" +
                                    if (wo.scheduledTime.isNotBlank())
                                        " • ${wo.scheduledTime}"
                                    else "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray
                            )
                        )
                        if (!wo.designFileUrl.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Archivo de diseño asociado",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = wo.designFileUrl,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = primaryBlue
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -------- SECCIÓN DE PLANIFICACIÓN --------
                PlannerPlanningSection(
                    workOrder = wo,
                    onPlanningSaved = { updated ->
                        workOrder = updated
                        Toast.makeText(
                            context,
                            "Planificación guardada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
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

/**
 * Sección donde el planner asigna operario, máquina y horario
 * y actualiza el estado de la OT.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlannerPlanningSection(
    workOrder: WorkOrder,
    onPlanningSaved: (WorkOrder) -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val primaryBlue = Color(0xFF1976D2)

    // -------- LISTA DE OPERARIOS --------
    var operarios by remember { mutableStateOf<List<OperarioBasic>>(emptyList()) }
    var isLoadingOperarios by remember { mutableStateOf(false) }
    var operariosError by remember { mutableStateOf<String?>(null) }

    // nombre mostrado
    var assignedOperator by remember { mutableStateOf(workOrder.assignedOperator ?: "") }
    // id real para Firestore (para calcular ocupados/disponibles)
    var selectedOperarioId by remember { mutableStateOf<String?>(null) }

    // Cargar lista de operarios (nuevos y antiguos)
    LaunchedEffect(Unit) {
        try {
            isLoadingOperarios = true
            operariosError = null

            val resultList = mutableListOf<OperarioBasic>()
            val usedIds = mutableSetOf<String>()

            // 1) Nuevos operarios desde collection "operators"
            //    (los registrados en la pantalla de Operarios)
            val operatorsSnap = db.collection("operators")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            operatorsSnap.documents.forEach { doc ->
                val id = doc.id
                val name = doc.getString("name") ?: doc.getString("nombre") ?: "Sin nombre"
                resultList.add(OperarioBasic(id, name))
                usedIds.add(id)
            }

            // 2) Operarios antiguos desde "users" (role = operario)
            val usersSnap = db.collection("users")
                .whereEqualTo("role", "operario")
                .get()
                .await()

            usersSnap.documents.forEach { doc ->
                val id = doc.id
                if (!usedIds.contains(id)) {
                    val name = doc.getString("name") ?: doc.getString("email") ?: "Sin nombre"
                    resultList.add(OperarioBasic(id, name))
                    usedIds.add(id)
                }
            }

            operarios = resultList

            // Si ya había un operario asignado por nombre, intentamos encontrar su id
            if (assignedOperator.isNotBlank()) {
                selectedOperarioId =
                    operarios.firstOrNull { it.name == assignedOperator }?.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
            operariosError = e.message ?: "Error al cargar operarios"
        } finally {
            isLoadingOperarios = false
        }
    }

    var assignedMachine by remember { mutableStateOf(workOrder.assignedMachine ?: "") }
    var plannedStartTimeRaw by remember { mutableStateOf(workOrder.plannedStartTime ?: "") }
    var plannedEndTimeRaw by remember { mutableStateOf(workOrder.plannedEndTime ?: "") }

    val plannedStartTime = formatTimeInput(plannedStartTimeRaw)
    val plannedEndTime = formatTimeInput(plannedEndTimeRaw)

    val statusOptions = listOf(
        "en registro",
        "planificada",
        "en progreso",
        "en ejecución",
        "por corregir",
        "completada"
    )
    var selectedStatus by remember { mutableStateOf(workOrder.status) }
    var isSaving by remember { mutableStateOf(false) }

    // Dropdown de operarios
    var expandedOperario by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Planificación y asignación",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Aquí el planner asigna operarios y máquinas, define la hora estimada y actualiza el estado de la OT.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF37474F)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // -------- Operario asignado (desplegable) --------
            ExposedDropdownMenuBox(
                expanded = expandedOperario,
                onExpandedChange = { expandedOperario = !expandedOperario }
            ) {
                OutlinedTextField(
                    value = if (assignedOperator.isNotBlank()) assignedOperator else "",
                    onValueChange = { },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    label = { Text("Operario asignado") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOperario)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expandedOperario,
                    onDismissRequest = { expandedOperario = false }
                ) {
                    when {
                        isLoadingOperarios -> {
                            DropdownMenuItem(
                                text = { Text("Cargando operarios...") },
                                onClick = { }
                            )
                        }

                        operariosError != null -> {
                            DropdownMenuItem(
                                text = { Text(operariosError!!) },
                                onClick = { }
                            )
                        }

                        operarios.isEmpty() -> {
                            DropdownMenuItem(
                                text = { Text("No hay operarios registrados") },
                                onClick = { }
                            )
                        }

                        else -> {
                            operarios.forEach { op ->
                                DropdownMenuItem(
                                    text = { Text(op.name) },
                                    onClick = {
                                        assignedOperator = op.name
                                        selectedOperarioId = op.id
                                        expandedOperario = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = assignedMachine,
                onValueChange = { assignedMachine = it },
                label = { Text("Máquina / recurso asignado") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = plannedStartTime,
                    onValueChange = { new ->
                        plannedStartTimeRaw = new.filter { it.isDigit() }
                    },
                    label = { Text("Inicio estimado (HH:mm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = plannedEndTime,
                    onValueChange = { new ->
                        plannedEndTimeRaw = new.filter { it.isDigit() }
                    },
                    label = { Text("Fin estimado (HH:mm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Estado de la OT",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // -------- Fila de estados SCROLEABLE --------
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusOptions.forEach { s ->
                    val selected = selectedStatus.equals(s, true)
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (selected) primaryBlue else Color.White,
                        shadowElevation = if (selected) 2.dp else 0.dp,
                        modifier = Modifier.clickable { selectedStatus = s }
                    ) {
                        Text(
                            text = s.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (selected) Color.White else Color.Black
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (assignedOperator.isBlank() || selectedOperarioId == null) {
                        Toast.makeText(
                            context,
                            "Asigna al menos un operario",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val finalStart = plannedStartTime
                    val finalEnd = plannedEndTime

                    scope.launch {
                        try {
                            isSaving = true

                            val updates = hashMapOf<String, Any?>(
                                "assignedOperator" to assignedOperator,
                                "operatorId" to selectedOperarioId, // <--- NUEVO
                                "assignedMachine" to assignedMachine,
                                "plannedStartTime" to finalStart,
                                "plannedEndTime" to finalEnd,
                                "status" to selectedStatus
                            )

                            db.collection("work-orders")
                                .document(workOrder.id)
                                .update(updates as Map<String, Any?>)
                                .await()

                            // Registrar evento en línea de tiempo
                            val auth = FirebaseAuth.getInstance()
                            val plannerId = auth.currentUser?.uid ?: "planner"

                            val timelineEvent = hashMapOf(
                                "workOrderId" to workOrder.id,
                                "workOrderTitle" to workOrder.title,
                                "clientName" to workOrder.clientName,
                                "type" to "planificacion",
                                "status" to selectedStatus,
                                "operator" to assignedOperator,
                                "operatorId" to selectedOperarioId,
                                "machine" to assignedMachine,
                                "plannedStartTime" to finalStart,
                                "plannedEndTime" to finalEnd,
                                "timestamp" to com.google.firebase.Timestamp.now(),
                                "plannerId" to plannerId
                            )

                            db.collection("timeline-events").add(timelineEvent).await()

                            val updatedWO = workOrder.copy(
                                assignedOperator = assignedOperator,
                                assignedMachine = assignedMachine,
                                plannedStartTime = finalStart,
                                plannedEndTime = finalEnd,
                                status = selectedStatus
                            )
                            onPlanningSaved(updatedWO)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                e.message ?: "Error al guardar planificación",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar planificación")
            }
        }
    }
}

// Formatea la entrada numérica a HH:mm (ej: "1345" -> "13:45")
private fun formatTimeInput(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(4)
    return when {
        digits.length <= 2 -> digits
        else -> digits.substring(0, 2) + ":" + digits.substring(2)
    }
}