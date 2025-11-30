package com.example.metalops.ui.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val dni: String?,
    val phone: String?
)

@Composable
fun AdminUsersScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val db = remember { FirebaseFirestore.getInstance() }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var users by remember { mutableStateOf<List<AdminUser>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null

            val snap = db.collection("users")
                .get()
                .await()

            users = snap.documents.mapNotNull { doc ->
                val email = doc.getString("email") ?: return@mapNotNull null

                val name = doc.getString("name")
                    ?: doc.getString("nombre")
                    ?: email.substringBefore("@")

                val rawRole = doc.getString("role")
                    ?: doc.getString("rol")
                    ?: "agente"

                val cleanRole = rawRole.replace("\"", "").lowercase()

                val dni = doc.getString("dni")
                val phone = doc.getString("phone") ?: doc.getString("telefono")

                AdminUser(
                    id = doc.id,
                    name = name,
                    email = email,
                    role = cleanRole,
                    dni = dni,
                    phone = phone
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = e.message ?: "Error al cargar usuarios"
        } finally {
            isLoading = false
        }
    }

    val primaryBlue = Color(0xFF1976D2)

    val total = users.size
    val planners = users.count { it.role.equals("planner", true) }
    val agentes = users.count { it.role.equals("agente", true) }
    val operarios = users.count { it.role.equals("operario", true) }
    val admins = users.count { it.role.equals("admin", true) }

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
                text = "Usuarios",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Gestión de usuarios registrados en MetalOps.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Resumen rápido ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UsersSummaryCard("Total", total.toString(), true, Modifier.weight(1f))
                UsersSummaryCard("Planners", planners.toString(), false, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UsersSummaryCard("Agentes", agentes.toString(), false, Modifier.weight(1f))
                UsersSummaryCard("Operarios", operarios.toString(), false, Modifier.weight(1f))
            }

            if (admins > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                UsersSummaryCard(
                    title = "Admins",
                    value = admins.toString(),
                    highlighted = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Listado de usuarios",
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

            if (users.isEmpty() && !isLoading && errorMessage == null) {
                Text(
                    text = "Aún no hay usuarios registrados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(users) { user ->
                        AdminUserRow(user = user, primaryBlue = primaryBlue)
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
private fun UsersSummaryCard(
    title: String,
    value: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val blue = Color(0xFF1976D2)
    Card(
        modifier = modifier.height(70.dp),
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
private fun AdminUserRow(
    user: AdminUser,
    primaryBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "Rol: ${user.role.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall.copy(color = primaryBlue)
            )
            Text(
                text = "Correo: ${user.email}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
            user.dni?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = "DNI: $it",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }
            user.phone?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = "Teléfono: $it",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }
        }
    }
}
