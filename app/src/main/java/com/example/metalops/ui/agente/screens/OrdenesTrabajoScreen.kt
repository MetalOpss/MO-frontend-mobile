package com.example.metalops.ui.agente.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

// ----- MODELOS -----
data class ClientBasic(
    val id: String,
    val name: String
)

data class WorkOrderBasic(
    val id: String,
    val code: String,
    val title: String,
    val clientName: String,
    val status: String,
    val scheduledDate: String,
    val priority: String
)

@Composable
fun OrdenesTrabajoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val primaryBlue = Color(0xFF1976D2)

    // -------- ESTADO LISTADO OT --------
    var orders by remember { mutableStateOf<List<WorkOrderBasic>>(emptyList()) }
    var isOrdersLoading by remember { mutableStateOf(true) }
    var ordersError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Filtro por estado (scroll horizontal)
    val statusFilters = listOf(
        "Todas",
        "en registro",
        "pendiente",
        "asignada",
        "en proceso",
        "completada",
        "cerrada"
    )
    var selectedStatusFilter by remember { mutableStateOf("Todas") }

    // -------- ESTADO CLIENTES --------
    var clients by remember { mutableStateOf<List<ClientBasic>>(emptyList()) }
    var isClientsLoading by remember { mutableStateOf(true) }
    var clientsError by remember { mutableStateOf<String?>(null) }

    // -------- MODAL CREACIÓN OT --------
    var showCreateDialog by remember { mutableStateOf(false) }

    // -------- INPUTS FORMULARIO OT --------
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var scheduledDate by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("media") }
    var isUrgent by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("en registro") }

    var selectedClient by remember { mutableStateOf<ClientBasic?>(null) }
    var clientMenuExpanded by remember { mutableStateOf(false) }

    // ARCHIVO (simulado)
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedFileUri = uri
        selectedFileName = uri?.lastPathSegment ?: "archivo_seleccionado"
    }

    var isSaving by remember { mutableStateOf(false) }

    // -------- DATE PICKER --------
    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val mm = (m + 1).toString().padStart(2, '0')
                val dd = d.toString().padStart(2, '0')
                scheduledDate = "$y-$mm-$dd"
            },
            year,
            month,
            day
        ).show()
    }

    // -------- CARGA INICIAL: ÓRDENES + CLIENTES --------
    LaunchedEffect(Unit) {
        // Carga de clientes
        try {
            isClientsLoading = true
            clientsError = null

            val snap = db.collection("clients")
                .orderBy("name")
                .get()
                .await()

            clients = snap.documents.mapNotNull { doc ->
                val name = doc.getString("name") ?: doc.getString("cliente")
                if (name != null) ClientBasic(doc.id, name) else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            clientsError = e.message ?: "Error al cargar clientes"
        } finally {
            isClientsLoading = false
        }

        // Carga de órdenes de trabajo
        try {
            isOrdersLoading = true
            ordersError = null

            val snap = db.collection("work-orders")
                .get()
                .await()

            orders = snap.documents.map { doc ->
                WorkOrderBasic(
                    id = doc.id,
                    code = doc.getString("code") ?: "SIN-CODIGO",
                    title = doc.getString("title") ?: "Sin título",
                    clientName = doc.getString("clientName") ?: "Sin cliente",
                    status = doc.getString("status") ?: "desconocido",
                    scheduledDate = doc.getString("scheduledDate") ?: "-",
                    priority = doc.getString("priority") ?: "media"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ordersError = e.message ?: "Error al cargar órdenes"
        } finally {
            isOrdersLoading = false
        }
    }

    val filteredOrders = remember(orders, searchQuery, selectedStatusFilter) {
        orders
            .filter {
                if (searchQuery.isBlank()) true
                else it.title.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true) ||
                        it.clientName.contains(searchQuery, ignoreCase = true)
            }
            .filter {
                selectedStatusFilter == "Todas" ||
                        it.status.equals(selectedStatusFilter, ignoreCase = true)
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Encabezado global
            AppHeader()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Órdenes de trabajo (Agente)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Gestiona tus OT existentes y registra nuevas órdenes.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -------- BUSCADOR --------
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar órdenes") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // -------- FILTRO DE ESTADO (SCROLL HORIZONTAL) --------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusFilters.forEach { statusFilter ->
                    FilterChip(
                        selected = selectedStatusFilter == statusFilter,
                        onClick = { selectedStatusFilter = statusFilter },
                        label = {
                            Text(if (statusFilter == "Todas") "Todos" else statusFilter)
                        },
                        leadingIcon = if (selectedStatusFilter == statusFilter) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryBlue.copy(alpha = 0.12f),
                            selectedLabelColor = primaryBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -------- LISTA DE CARDS DE OT --------
            if (isOrdersLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (ordersError != null) {
                Text(
                    text = ordersError ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (filteredOrders.isEmpty()) {
                Text(
                    text = "No hay órdenes registradas.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = true)
                ) {
                    filteredOrders.forEach { ot ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = ot.code,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryBlue
                                    )
                                )
                                Text(
                                    text = ot.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Cliente: ${ot.clientName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Fecha: ${ot.scheduledDate.ifBlank { "-" }}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Estado: ${ot.status} · Prioridad: ${ot.priority}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------- BOTÓN PARA ABRIR MODAL DE REGISTRO --------
            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar OT")
            }
        }

        // -------- DIALOG / MODAL DE CREACIÓN --------
        if (showCreateDialog) {
            Dialog(
                onDismissRequest = {
                    if (!isSaving) showCreateDialog = false
                }
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nueva orden de trabajo",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Completa los datos para registrar la OT.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )

                            // ---- TÍTULO ----
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Título / descripción corta") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Title,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // ---- CLIENTE (DROPDOWN) ----
                            Text(
                                text = "Cliente",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            if (isClientsLoading) {
                                Text("Cargando clientes...", color = Color.Gray)
                            } else if (clientsError != null) {
                                Text(
                                    text = clientsError!!,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else if (clients.isEmpty()) {
                                Text(
                                    text = "No hay clientes registrados.",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Box {
                                // campo visual
                                OutlinedTextField(
                                    value = selectedClient?.name ?: "",
                                    onValueChange = { },
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("Selecciona un cliente") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                // capa clickable completa
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable {
                                            if (clients.isEmpty()) {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "No hay clientes registrados",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else {
                                                clientMenuExpanded = !clientMenuExpanded
                                            }
                                        }
                                )
                                DropdownMenu(
                                    expanded = clientMenuExpanded && clients.isNotEmpty(),
                                    onDismissRequest = { clientMenuExpanded = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    clients.forEach { client ->
                                        DropdownMenuItem(
                                            text = { Text(client.name) },
                                            onClick = {
                                                selectedClient = client
                                                clientMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // ---- UBICACIÓN ----
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                label = { Text("Ubicación / planta") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // ---- FECHA Y HORA ----
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = scheduledDate,
                                        onValueChange = { },
                                        readOnly = true,
                                        enabled = false,
                                        label = { Text("Fecha (YYYY-MM-DD)") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = null
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { openDatePicker() }
                                    )
                                }
                                OutlinedTextField(
                                    value = scheduledTime,
                                    onValueChange = { scheduledTime = it },
                                    label = { Text("Hora (HH:mm)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // ---- PRIORIDAD ----
                            Text(
                                text = "Prioridad",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            var priorityMenuExpanded by remember { mutableStateOf(false) }
                            val priorityOptions = listOf("baja", "media", "alta")

                            Box {
                                OutlinedTextField(
                                    value = priority.replaceFirstChar { it.uppercase() },
                                    onValueChange = { },
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("Selecciona prioridad") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { priorityMenuExpanded = !priorityMenuExpanded }
                                )
                                DropdownMenu(
                                    expanded = priorityMenuExpanded,
                                    onDismissRequest = { priorityMenuExpanded = false }
                                ) {
                                    priorityOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(option.replaceFirstChar { it.uppercase() })
                                            },
                                            onClick = {
                                                priority = option
                                                priorityMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // ---- ESTADO ----
                            Text(
                                text = "Estado",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            var statusMenuExpanded by remember { mutableStateOf(false) }
                            val statusOptions = listOf(
                                "en registro",
                                "pendiente",
                                "asignada",
                                "en proceso",
                                "completada",
                                "cerrada"
                            )

                            Box {
                                OutlinedTextField(
                                    value = status.replaceFirstChar { it.uppercase() },
                                    onValueChange = { },
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("Selecciona estado") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { statusMenuExpanded = !statusMenuExpanded }
                                )
                                DropdownMenu(
                                    expanded = statusMenuExpanded,
                                    onDismissRequest = { statusMenuExpanded = false }
                                ) {
                                    statusOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(option.replaceFirstChar { it.uppercase() })
                                            },
                                            onClick = {
                                                status = option
                                                statusMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // ---- ARCHIVO (SIMULADO) ----
                            Column {
                                Text(
                                    text = "Archivo de diseño (opcional)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { pickFileLauncher.launch("*/*") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = primaryBlue
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedFileName ?: "Simular adjuntar archivo"
                                    )
                                }
                            }

                            // ---- BOTONES DEL MODAL ----
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (!isSaving) {
                                            showCreateDialog = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }

                                Button(
                                    onClick = {
                                        if (title.isBlank()) {
                                            Toast.makeText(
                                                context,
                                                "Ingresa un título para la OT",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }
                                        if (selectedClient == null) {
                                            Toast.makeText(
                                                context,
                                                "Selecciona un cliente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }

                                        scope.launch {
                                            try {
                                                isSaving = true

                                                val docRef =
                                                    db.collection("work-orders").document()

                                                val designUrl: String? = selectedFileName

                                                val data = hashMapOf<String, Any?>(
                                                    "title" to title,
                                                    "clientName" to selectedClient!!.name,
                                                    "clientId" to selectedClient!!.id,
                                                    "location" to location,
                                                    "priority" to priority,
                                                    "status" to status,
                                                    "type" to if (isUrgent) "urgente" else "normal",
                                                    "scheduledDate" to scheduledDate,
                                                    "scheduledTime" to scheduledTime,
                                                    "isUrgent" to isUrgent,
                                                    "plannerId" to null,
                                                    "correctionOf" to null,
                                                    "errorFlag" to false,
                                                    "errorMessage" to null,
                                                    "designFileUrl" to designUrl
                                                )

                                                data["code"] =
                                                    "OT-${docRef.id.takeLast(4).uppercase()}"

                                                docRef.set(data).await()

                                                Toast.makeText(
                                                    context,
                                                    "OT registrada correctamente",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                // limpiamos formulario
                                                title = ""
                                                location = ""
                                                scheduledDate = ""
                                                scheduledTime = ""
                                                selectedClient = null
                                                selectedFileUri = null
                                                selectedFileName = null
                                                isUrgent = false
                                                priority = "media"
                                                status = "en registro"

                                                showCreateDialog = false

                                                // recargar listado
                                                isOrdersLoading = true
                                                val snap =
                                                    db.collection("work-orders").get().await()
                                                orders = snap.documents.map { doc ->
                                                    WorkOrderBasic(
                                                        id = doc.id,
                                                        code = doc.getString("code")
                                                            ?: "SIN-CODIGO",
                                                        title = doc.getString("title")
                                                            ?: "Sin título",
                                                        clientName = doc.getString("clientName")
                                                            ?: "Sin cliente",
                                                        status = doc.getString("status")
                                                            ?: "desconocido",
                                                        scheduledDate = doc.getString("scheduledDate")
                                                            ?: "-",
                                                        priority = doc.getString("priority")
                                                            ?: "media"
                                                    )
                                                }
                                                isOrdersLoading = false

                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Toast.makeText(
                                                    context,
                                                    e.message ?: "Error al registrar OT",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                isOrdersLoading = false
                                            } finally {
                                                isSaving = false
                                            }
                                        }
                                    },
                                    enabled = !isSaving,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryBlue
                                    )
                                ) {
                                    if (isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                        )
                                    } else {
                                        Text("Guardar OT")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}