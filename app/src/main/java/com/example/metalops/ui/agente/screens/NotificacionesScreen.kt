package com.example.metalops.ui.agente.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.data.model.AgentNotification
import kotlinx.coroutines.delay

@Composable
fun NotificacionesScreen(
    navController: NavHostController
) {
    val primaryBlue = Color(0xFF1976D2)
    val lightBackground = Color(0xFFF9F6FF)

    var isLoading by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf<List<AgentNotification>>(emptyList()) }
    var selectedTab by remember { mutableStateOf("No leídas") }

    // ============================
    // 🔵 Datos fijos: 5 no leídas + 5 leídas
    // ============================
    LaunchedEffect(Unit) {
        isLoading = true
        delay(600) // simulamos una pequeña carga

        notifications = listOf(
            // ---- NO LEÍDAS (5) ----
            AgentNotification(
                id = "1",
                title = "Nueva OT registrada",
                body = "Se creó la OT · Corte de placas para el cliente MetalTech.",
                isRead = false,
                createdAt = "Hoy · 08:30"
            ),
            AgentNotification(
                id = "2",
                title = "Observación del planner",
                body = "Planner agregó un comentario en OT · Reparación de estructura.",
                isRead = false,
                createdAt = "Hoy · 09:10"
            ),
            AgentNotification(
                id = "3",
                title = "Cliente envió información adicional",
                body = "Cliente anexó plano actualizado para OT · Montaje de soporte.",
                isRead = false,
                createdAt = "Hoy · 10:05"
            ),
            AgentNotification(
                id = "4",
                title = "Cambio de prioridad",
                body = "OT · Cambio de motor marcada como urgente.",
                isRead = false,
                createdAt = "Hoy · 11:20"
            ),
            AgentNotification(
                id = "5",
                title = "Falta completar datos",
                body = "Revisa la dirección en OT · Instalación de barandas.",
                isRead = false,
                createdAt = "Ayer · 18:45"
            ),

            // ---- LEÍDAS (5) ----
            AgentNotification(
                id = "6",
                title = "OT aprobada por planner",
                body = "OT · Mantenimiento preventivo lista para asignar operarios.",
                isRead = true,
                createdAt = "Hace 2 días · 09:00"
            ),
            AgentNotification(
                id = "7",
                title = "OT rechazada",
                body = "OT · Reparación de bomba rechazada por datos incompletos.",
                isRead = true,
                createdAt = "Hace 2 días · 15:30"
            ),
            AgentNotification(
                id = "8",
                title = "Cliente confirmó fecha",
                body = "Fecha confirmada para OT · Inspección de seguridad.",
                isRead = true,
                createdAt = "Hace 3 días · 16:10"
            ),
            AgentNotification(
                id = "9",
                title = "OT completada",
                body = "Operario marcó como finalizada OT · Ajuste de calibración.",
                isRead = true,
                createdAt = "Hace 4 días · 12:05"
            ),
            AgentNotification(
                id = "10",
                title = "Turno cerrado",
                body = "Se cerró el turno asociado a OT · Limpieza de área.",
                isRead = true,
                createdAt = "Hace 5 días · 07:50"
            )
        )

        isLoading = false
    }

    val filteredNotifications by remember(notifications, selectedTab) {
        mutableStateOf(
            when (selectedTab) {
                "Leídas" -> notifications.filter { it.isRead }
                "No leídas" -> notifications.filter { !it.isRead }
                else -> notifications
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable { navController.popBackStack() }, // cerrar al tocar fuera
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = lightBackground
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clickable(enabled = false) {}
            ) {
                // Encabezado
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
                            text = "Buzón de notificaciones",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray
                            )
                        )
                    }

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs Leídas / No leídas
                NotificationTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    primaryBlue = primaryBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredNotifications.isEmpty() && !isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay notificaciones en esta sección.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredNotifications) { notification ->
                            NotificationItem(
                                notification = notification,
                                primaryBlue = primaryBlue,
                                onToggleRead = { target, newIsRead ->
                                    // Solo actualizamos en memoria (sin Firestore)
                                    notifications = notifications.map {
                                        if (it.id == target.id) it.copy(isRead = newIsRead) else it
                                    }
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
                    CircularProgressIndicator(color = primaryBlue)
                }
            }
        }
    }
}

/* ---------- TABS LEÍDAS / NO LEÍDAS ---------- */

@Composable
private fun NotificationTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    primaryBlue: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotificationTabChip(
            text = "Leídas",
            isSelected = selectedTab == "Leídas",
            primaryBlue = primaryBlue,
            onClick = { onTabSelected("Leídas") }
        )
        NotificationTabChip(
            text = "No leídas",
            isSelected = selectedTab == "No leídas",
            primaryBlue = primaryBlue,
            onClick = { onTabSelected("No leídas") }
        )
    }
}

@Composable
private fun NotificationTabChip(
    text: String,
    isSelected: Boolean,
    primaryBlue: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isSelected) primaryBlue else Color.White,
        modifier = Modifier
            .height(36.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) Color.White else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            )
        }
    }
}

/* ---------- ITEM NOTIFICACIÓN ---------- */

@Composable
private fun NotificationItem(
    notification: AgentNotification,
    primaryBlue: Color,
    onToggleRead: (AgentNotification, Boolean) -> Unit
) {
    val cardColor = Color(0xFFE7E9F7)
    val eyeScale by animateFloatAsState(
        targetValue = if (notification.isRead) 1.0f else 1.1f,
        animationSpec = tween(160),
        label = "eyeScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDDE8FF),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificación",
                        tint = primaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (notification.createdAt.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = notification.createdAt,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

            IconButton(
                onClick = { onToggleRead(notification, !notification.isRead) },
                modifier = Modifier.scale(eyeScale)
            ) {
                Icon(
                    imageVector = if (notification.isRead)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = if (notification.isRead)
                        "Marcar como no leída"
                    else
                        "Marcar como leída",
                    tint = primaryBlue
                )
            }
        }
    }
}