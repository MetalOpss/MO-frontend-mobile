package com.example.metalops.ui.operario.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ConfiguracionOperarioScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val primaryBlue = Color(0xFF1976D2)

    // Estados locales
    var notificacionesActivas by remember { mutableStateOf(true) }
    var sonidoNotificaciones by remember { mutableStateOf(true) }
    var vibracion by remember { mutableStateOf(true) }
    var mostrarTurnosCompletados by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Encabezado
                Text(
                    text = "Configuración",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                )
                Text(
                    text = "Ajustes rápidos para tu experiencia como operario.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider()

                // NOTIFICACIONES
                Text(
                    text = "Notificaciones",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                SettingRow(
                    title = "Notificaciones generales",
                    subtitle = "Recibir alertas de nuevos turnos, cambios de OT y avisos importantes.",
                    checked = notificacionesActivas,
                    onCheckedChange = { notificacionesActivas = it },
                    icon = if (notificacionesActivas) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                    primaryBlue = primaryBlue
                )

                SettingRow(
                    title = "Sonido",
                    subtitle = "Reproducir tono cuando llegue una nueva notificación.",
                    checked = sonidoNotificaciones,
                    onCheckedChange = { sonidoNotificaciones = it },
                    icon = Icons.Default.Notifications,
                    primaryBlue = primaryBlue
                )

                SettingRow(
                    title = "Vibración",
                    subtitle = "Vibrar el dispositivo con avisos críticos.",
                    checked = vibracion,
                    onCheckedChange = { vibracion = it },
                    icon = Icons.Default.Vibration,
                    primaryBlue = primaryBlue
                )

                Spacer(modifier = Modifier.height(4.dp))
                Divider()

                // TURNOS
                Text(
                    text = "Turnos y vista",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                SettingRow(
                    title = "Mostrar turnos completados",
                    subtitle = "Mantener en la lista los turnos antiguos para consultar historial reciente.",
                    checked = mostrarTurnosCompletados,
                    onCheckedChange = { mostrarTurnosCompletados = it },
                    icon = Icons.Default.Schedule,
                    primaryBlue = primaryBlue
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider()

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Preferencias guardadas",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue
                        )
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

// COMPONENTE DE FILA CONFIG
@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryBlue: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryBlue
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryBlue
            )
        )
    }
}