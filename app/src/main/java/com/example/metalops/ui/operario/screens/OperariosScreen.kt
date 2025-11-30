package com.example.metalops.ui.planner.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog   // ⬅️ IMPORT NECESARIO
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ----- MODELO DE OPERARIO -----
data class Operator(
    val id: String,
    val dni: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val specialty: String?,
    val shift: String?,
    val activeOtCount: Int
)

@Composable
fun OperariosScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val primaryBlue = Color(0xFF1976D2)
    val successGreen = Color(0xFF4CAF50)
    val lightGray = Color(0xFFF5F5F5)

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userName = currentUser?.displayName ?: currentUser?.email ?: "Planner"
    val userEmail = currentUser?.email

    var operators by remember { mutableStateOf<List<Operator>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Modal crear operario
    var showCreateDialog by remember { mutableStateOf(false) }
    var dni by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var shift by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Buscador
    var searchQuery by remember { mutableStateOf("") }

    // --- CARGA DE OPERARIOS + OTs ACTIVAS ---
    suspend fun loadOperators() {
        try {
            isLoading = true
            error = null

            val operatorsSnap = db.collection("operators").get().await()

            val activeStatuses = listOf(
                "en registro",
                "planificada",
                "en progreso",
                "en ejecución",
                "por corregir",
                "pendiente",
                "asignada",
                "en proceso"
            )

            val workSnap = db.collection("work-orders")
                .whereIn("status", activeStatuses)
                .get()
                .await()

            val workDocs = workSnap.documents

            operators = operatorsSnap.documents.map { doc ->
                val id = doc.id
                val dniValue = doc.getString("dni") ?: ""
                val nameValue = doc.getString("name") ?: doc.getString("nombre") ?: "Sin nombre"
                val phoneValue = doc.getString("phone") ?: doc.getString("telefono")
                val emailValue = doc.getString("email") ?: ""
                val specialtyValue = doc.getString("specialty") ?: doc.getString("especialidad")
                val shiftValue = doc.getString("shift") ?: doc.getString("turno")

                val byId = workDocs.count { it.getString("operatorId") == id }
                val byName = workDocs.count {
                    it.getString("operatorId").isNullOrBlank() &&
                            (it.getString("assignedOperator") ?: "") == nameValue
                }
                val activeCount = byId + byName

                Operator(
                    id = id,
                    dni = dniValue,
                    name = nameValue,
                    phone = phoneValue,
                    email = emailValue,
                    specialty = specialtyValue,
                    shift = shiftValue,
                    activeOtCount = activeCount
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error = e.message ?: "Error al cargar operarios"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadOperators()
    }

    val occupiedCount = operators.count { it.activeOtCount > 0 }
    val availableCount = operators.count { it.activeOtCount == 0 }

    val filteredOperators = remember(operators, searchQuery) {
        operators.filter { op ->
            if (searchQuery.isBlank()) true
            else {
                val q = searchQuery.trim()
                op.name.contains(q, ignoreCase = true) ||
                        op.dni.contains(q, ignoreCase = true) ||
                        (op.specialty?.contains(q, ignoreCase = true) == true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(lightGray)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppHeader()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido, $userName",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            )
            if (userEmail != null) {
                Text(
                    text = "Sesión: $userEmail",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Operarios",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            )
            Text(
                text = "Gestión del personal en operaciones.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Ocupados",
                    value = occupiedCount.toString(),
                    valueColor = Color(0xFF1E88E5),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Disponibles",
                    value = availableCount.toString(),
                    valueColor = successGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar operario") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Listado de operarios",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (filteredOperators.isEmpty()) {
                Text(
                    text = "No se encontraron operarios.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredOperators.forEach { op ->
                        OperatorCard(
                            operator = op,
                            primaryBlue = primaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar operario")
            }
        }

        if (showCreateDialog) {
            Dialog(onDismissRequest = { if (!isSaving) showCreateDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Registrar nuevo operario",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Completa los datos del operario.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )

                        OutlinedTextField(
                            value = dni,
                            onValueChange = { dni = it },
                            label = { Text("DNI") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre completo") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo electrónico") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = specialty,
                            onValueChange = { specialty = it },
                            label = { Text("Especialidad (mecánico, eléctrico, etc.)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = shift,
                            onValueChange = { shift = it },
                            label = { Text("Turno (día, noche, mixto)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (!isSaving) showCreateDialog = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    if (dni.isBlank() || name.isBlank()) {
                                        Toast.makeText(
                                            context,
                                            "DNI y nombre son obligatorios",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    scope.launch {
                                        try {
                                            isSaving = true

                                            val data = hashMapOf(
                                                "dni" to dni,
                                                "name" to name,
                                                "phone" to phone,
                                                "email" to email,
                                                "specialty" to specialty,
                                                "shift" to shift,
                                                "isActive" to true
                                            )

                                            db.collection("operators").add(data).await()

                                            Toast.makeText(
                                                context,
                                                "Operario registrado correctamente",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            dni = ""
                                            name = ""
                                            phone = ""
                                            email = ""
                                            specialty = ""
                                            shift = ""

                                            showCreateDialog = false
                                            loadOperators()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(
                                                context,
                                                e.message
                                                    ?: "Error al registrar operario",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } finally {
                                            isSaving = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !isSaving,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue
                                )
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----- COMPONENTES REUTILIZABLES -----
@Composable
private fun SummaryCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = valueColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = valueColor
                    )
                )
            }
        }
    }
}

@Composable
private fun OperatorCard(
    operator: Operator,
    primaryBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = operator.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "DNI: ${operator.dni}",
                style = MaterialTheme.typography.bodySmall
            )
            if (!operator.specialty.isNullOrBlank()) {
                Text(
                    text = "Especialidad: ${operator.specialty}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!operator.shift.isNullOrBlank()) {
                Text(
                    text = "Turno: ${operator.shift}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!operator.phone.isNullOrBlank()) {
                Text(
                    text = "Teléfono: ${operator.phone}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!operator.email.isNullOrBlank()) {
                Text(
                    text = "Correo: ${operator.email}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val isBusy = operator.activeOtCount > 0
            val statusText =
                if (isBusy) "Ocupado (${operator.activeOtCount} OT)" else "Disponible"
            val statusColor =
                if (isBusy) Color(0xFFF57C00) else primaryBlue

            Box(
                modifier = Modifier
                    .background(
                        color = statusColor.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                )
            }
        }
    }
}