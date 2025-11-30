package com.example.metalops.ui.admin.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.ui.navigation.RootRoute
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminConfigScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val primaryBlue = Color(0xFF1976D2)
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val notificacionesGlobales = remember { mutableStateOf(true) }
    val bloqueoEdicion = remember { mutableStateOf(false) }

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
                text = "Reportes / Configuración",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Espacio para reportes globales, exportación y ajustes avanzados.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Ajustes globales (simulados) ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificaciones globales",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Activar/desactivar notificaciones para todos los usuarios.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                        Switch(
                            checked = notificacionesGlobales.value,
                            onCheckedChange = { notificacionesGlobales.value = it },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = primaryBlue,
                                checkedThumbColor = Color.White
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bloquear edición de OT",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Ejemplo de bloqueo de cambios en producción.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                        Switch(
                            checked = bloqueoEdicion.value,
                            onCheckedChange = { bloqueoEdicion.value = it },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = primaryBlue,
                                checkedThumbColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sesión",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Cerrar sesión del administrador",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Cierra la sesión actual y vuelve a la pantalla de inicio de sesión.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )

                    Button(
                        onClick = {
                            auth.signOut()
                            Toast.makeText(
                                context,
                                "Sesión cerrada",
                                Toast.LENGTH_SHORT
                            ).show()

                            navController.navigate(RootRoute.Auth.route) {
                                popUpTo(RootRoute.Auth.route) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }
    }
}