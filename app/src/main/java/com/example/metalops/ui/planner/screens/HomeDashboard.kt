package com.example.metalops.ui.planner.screens

import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeDashboard(
    navController: NavController,
    fullName: String = "<Fullname>"
) {
    val dateText = getSpanishDate() // "Jueves, 4 de septiembre"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Título y fecha
        Text(
            text = "¡Bienvenido, $fullName!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Row superior: dos tarjetas
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallRoundedStatCard(
                title = "Ot's Activas",
                value = "24",
                subtitle = "+ 12% del mes anterior",
                modifier = Modifier.weight(1f)
            )
            SmallRoundedStatCard(
                title = "Maq. Activas",
                value = "24",
                subtitle = "82% utilización",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta única debajo (alineada a la izquierda, ocupa aproximadamente la mitad)
        Row(modifier = Modifier.fillMaxWidth()) {
            SmallRoundedStatCard(
                title = "Entregas hoy",
                value = "7",
                subtitle = "3 pendientes",
                modifier = Modifier
                    .weight(0.5f)
            )
            Spacer(modifier = Modifier.weight(0.5f)) // espacio a la derecha
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Producción Semanal (contenedor grande con border redondeado)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
                Text(
                    text = "Producción Semanal",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Text",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Placeholder para el gráfico / imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
                        .background(color = Color(0xFFF6F8FB), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Aquí puedes insertar tu gráfico (Chart) o reemplazar todo este Box por:
                    // Image(painter = painterResource(id = R.drawable.tu_imagen), contentDescription = null)
                    // o integrar MPAndroidChart/Compose Chart library.
                    Text(text = "Chart placeholder (imagen / librería aquí)", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // espacio para que no tape el bottom bar
    }
}

@Composable
private fun SmallRoundedStatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 26.sp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8A8A8A), fontSize = 12.sp))
        }
    }
}

// Fecha en español (ej: "Jueves, 4 de septiembre")
private fun getSpanishDate(): String {
    return try {
        val sdf = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
        val raw = sdf.format(Date())
        raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }
    } catch (e: Exception) {
        "Jueves, 4 de septiembre"
    }
}
