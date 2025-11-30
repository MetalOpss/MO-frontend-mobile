package com.example.metalops.ui.planner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ConfiguracionPlannerScreen(
    navController: NavHostController
) {
    var pinEnabled by remember { mutableStateOf(false) }
    var autoLockEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoUpdatesEnabled by remember { mutableStateOf(true) }

    // ----- MODAL IGUAL QUE AGENTE -----
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF7F5FF)
            ),
            elevation = CardDefaults.cardElevation(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
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
                            text = "Configuración",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Preferencias de la aplicación (Planner)",
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

                Text(
                    text = "Seguridad",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingRowPlanner(
                    icon = Icons.Default.Security,
                    iconBg = Color(0xFF1976D2),
                    title = "PIN de acceso",
                    subtitle = if (pinEnabled) "PIN activado" else "PIN desactivado"
                ) {
                    Switch(
                        checked = pinEnabled,
                        onCheckedChange = { pinEnabled = it }
                    )
                }

                SettingRowPlanner(
                    icon = Icons.Default.Lock,
                    iconBg = Color(0xFF1976D2),
                    title = "Cierre automático",
                    subtitle = if (autoLockEnabled) "Se bloquea tras inactividad" else "Desactivado"
                ) {
                    Switch(
                        checked = autoLockEnabled,
                        onCheckedChange = { autoLockEnabled = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Preferencias avanzadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingRowPlanner(
                    icon = Icons.Default.NotificationsActive,
                    iconBg = Color(0xFF1976D2),
                    title = "Notificaciones",
                    subtitle = if (notificationsEnabled) "Notificaciones activas" else "Notificaciones desactivadas"
                ) {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }

                SettingRowPlanner(
                    icon = Icons.Default.Lock,
                    iconBg = Color(0xFF1976D2),
                    title = "Auto-actualizaciones",
                    subtitle = if (autoUpdatesEnabled) "Auto-actualizaciones activas" else "Actualización manual"
                ) {
                    Switch(
                        checked = autoUpdatesEnabled,
                        onCheckedChange = { autoUpdatesEnabled = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRowPlanner(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        trailing()
    }
}