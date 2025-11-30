package com.example.metalops.ui.operario.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp

// --------------------------------------------------------
// MODELO UI
// --------------------------------------------------------
data class OperarioNotificationUi(
    val id: String,
    val message: String,
    val workOrderTitle: String,
    val status: String,
    val timestamp: Timestamp,
    val read: Boolean
)

private enum class NotificationFilter {
    UNREAD, READ
}

// --------------------------------------------------------
// PANTALLA PRINCIPAL
// --------------------------------------------------------
@Composable
fun OperarioNotificationsScreen(
    navController: NavHostController
) {
    var isLoading by remember { mutableStateOf(true) }
    var currentFilter by remember { mutableStateOf(NotificationFilter.UNREAD) }

    // =====================================================
    // 🔵 NOTIFICACIONES FALSAS: 5 NO LEÍDAS + 5 LEÍDAS
    // =====================================================
    val fakeNotifications = remember {
        listOf(
            // ----- NO LEÍDAS (5) -----
            OperarioNotificationUi("1", "Nueva OT asignada", "OT · Corte láser", "asignada", Timestamp.now(), false),
            OperarioNotificationUi("2", "Actualización de OT", "OT · Reparación soldadura", "en progreso", Timestamp.now(), false),
            OperarioNotificationUi("3", "Inicio de turno programado", "OT · Mantenimiento preventivo", "programada", Timestamp.now(), false),
            OperarioNotificationUi("4", "Se requiere confirmación", "OT · Fabricación soporte", "pendiente", Timestamp.now(), false),
            OperarioNotificationUi("5", "OT marcada como urgente", "OT · Urgente: cambio de motor", "urgente", Timestamp.now(), false),

            // ----- LEÍDAS (5) -----
            OperarioNotificationUi("6", "OT completada", "OT · Instalación de paneles", "completada", Timestamp.now(), true),
            OperarioNotificationUi("7", "Cambio de horario", "OT · Ensamblaje estructura", "reprogramada", Timestamp.now(), true),
            OperarioNotificationUi("8", "Nueva observación del planner", "OT · Reparación eléctrica", "observación", Timestamp.now(), true),
            OperarioNotificationUi("9", "OT en revisión", "OT · Alineamiento mecánico", "revisión", Timestamp.now(), true),
            OperarioNotificationUi("10", "Turno finalizado", "OT · Ajuste de calibración", "finalizada", Timestamp.now(), true)
        )
    }

    // Simulamos delay de carga
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(600)
        isLoading = false
    }

    // Filtrar
    val filteredNotifications = when (currentFilter) {
        NotificationFilter.UNREAD -> fakeNotifications.filter { !it.read }
        NotificationFilter.READ -> fakeNotifications.filter { it.read }
    }

    val primaryColor = Color(0xFF0F1E3A)

    // --------------------------------------------------------
    // UI — Modal tipo "pantalla encima"
    // --------------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
            ) {

                // ---------- Header ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notificaciones",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Actualizaciones de tus órdenes asignadas",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ---------- Filtros ----------
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = currentFilter == NotificationFilter.UNREAD,
                        onClick = { currentFilter = NotificationFilter.UNREAD },
                        label = { Text("No leídas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = currentFilter == NotificationFilter.READ,
                        onClick = { currentFilter = NotificationFilter.READ },
                        label = { Text("Leídas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ---------- Contenido ----------
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                } else if (filteredNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentFilter == NotificationFilter.UNREAD)
                                "No tienes notificaciones sin leer."
                            else
                                "No tienes notificaciones leídas.",
                            color = Color.Gray
                        )
                    }

                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredNotifications) { notif ->
                            NotificationItem(
                                notif = notif,
                                primaryColor = primaryColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------
// ITEM DE LISTA
// --------------------------------------------------------
@Composable
private fun NotificationItem(
    notif: OperarioNotificationUi,
    primaryColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.read) Color(0xFFF5F5F5) else Color(0xFFE3F2FD)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = notif.workOrderTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = notif.message,
                style = MaterialTheme.typography.bodyMedium
            )

            if (notif.status.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Estado: ${notif.status}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (notif.read) "Leída" else "Nueva",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (notif.read) Color.Gray else primaryColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
