package com.example.metalops.ui.planner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

data class PlannerNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val isRead: Boolean = false,
    val timestamp: String = ""
)

@Composable
fun NotificacionesPlannerScreen(
    navController: NavHostController
) {
    var notifications by remember { mutableStateOf<List<PlannerNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf("no_leidas") } // "leidas" o "no_leidas"

    // ============================
    // 🔵 Datos fijos: 5 no leídas y 5 leídas
    // ============================
    LaunchedEffect(Unit) {
        isLoading = true
        delay(600) // simulamos pequeña carga

        notifications = listOf(
            // ---- NO LEÍDAS (5) ----
            PlannerNotification(
                id = "1",
                title = "Nueva OT enviada por Agente",
                body = "OT · Corte de placas enviada para planificación.",
                isRead = false,
                timestamp = "Hoy · 08:15"
            ),
            PlannerNotification(
                id = "2",
                title = "Operario asignado requiere confirmación",
                body = "Confirma a Jorge Luna para OT · Mantenimiento de bandas.",
                isRead = false,
                timestamp = "Hoy · 09:02"
            ),
            PlannerNotification(
                id = "3",
                title = "OT marcada como urgente",
                body = "OT · Cambio de motor registrada como urgente.",
                isRead = false,
                timestamp = "Hoy · 09:45"
            ),
            PlannerNotification(
                id = "4",
                title = "Cliente solicitó ajuste de fecha",
                body = "Actualiza la fecha de OT · Reparación de estructura.",
                isRead = false,
                timestamp = "Ayer · 18:10"
            ),
            PlannerNotification(
                id = "5",
                title = "Nueva OT en registro",
                body = "Revisa la OT · Instalación de barandas industriales.",
                isRead = false,
                timestamp = "Ayer · 17:40"
            ),

            // ---- LEÍDAS (5) ----
            PlannerNotification(
                id = "6",
                title = "OT completada por Operario",
                body = "OT · Inspección de seguridad finalizada.",
                isRead = true,
                timestamp = "Hace 2 días · 11:20"
            ),
            PlannerNotification(
                id = "7",
                title = "OT reprogramada",
                body = "OT · Revisión de válvulas movida al viernes.",
                isRead = true,
                timestamp = "Hace 2 días · 09:35"
            ),
            PlannerNotification(
                id = "8",
                title = "Nuevo comentario del cliente",
                body = "Cliente agregó detalles para OT · Ajuste de soporte.",
                isRead = true,
                timestamp = "Hace 3 días · 16:05"
            ),
            PlannerNotification(
                id = "9",
                title = "Operario marcó inicio de trabajo",
                body = "Sebastián Alcocer inició OT · Montaje de estructura.",
                isRead = true,
                timestamp = "Hace 4 días · 07:55"
            ),
            PlannerNotification(
                id = "10",
                title = "Turno de noche completado",
                body = "Operario reportó fin de turno en OT · Limpieza profunda.",
                isRead = true,
                timestamp = "Hace 5 días · 06:30"
            )
        )

        isLoading = false
    }

    val filtered = notifications.filter {
        if (selectedTab == "no_leidas") !it.isRead else it.isRead
    }

    // -------- MODAL IGUAL QUE ANTES --------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF7F5FF),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                // HEADER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Notificaciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Buzón de notificaciones (Planner)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TABS LEÍDAS / NO LEÍDAS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TabPill(
                        text = "Leídas",
                        selected = selectedTab == "leidas",
                        onClick = { selectedTab = "leidas" }
                    )
                    TabPill(
                        text = "No leídas",
                        selected = selectedTab == "no_leidas",
                        onClick = { selectedTab = "no_leidas" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    filtered.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay notificaciones en este filtro.")
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filtered) { notif ->
                                NotificationCard(
                                    notification = notif,
                                    onToggleRead = { current ->
                                        // Solo cambiamos el estado local (no hay Firestore)
                                        notifications = notifications.map {
                                            if (it.id == current.id)
                                                it.copy(isRead = !current.isRead)
                                            else it
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color(0xFF1976D2) else Color(0xFFE5E5F0))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.DarkGray,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun NotificationCard(
    notification: PlannerNotification,
    onToggleRead: (PlannerNotification) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8E7F4)
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (notification.body.isNotEmpty()) {
                    Text(
                        text = notification.body,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (notification.timestamp.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notification.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = { onToggleRead(notification) }) {
                Icon(
                    imageVector = if (notification.isRead)
                        Icons.Default.RemoveRedEye
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = if (notification.isRead) Color(0xFF1976D2) else Color.Gray
                )
            }
        }
    }
}
