package com.example.metalops.ui.planner.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun HomeDashboard(
    navController: NavController,
    fullName: String = "<Fullname>"
) {
    val dateText = getSpanishDate()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Encabezado
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

        // Tarjetas superiores
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

        Row(modifier = Modifier.fillMaxWidth()) {
            SmallRoundedStatCard(
                title = "Entregas hoy",
                value = "7",
                subtitle = "3 pendientes",
                modifier = Modifier.weight(0.5f)
            )
            Spacer(modifier = Modifier.weight(0.5f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Producción semanal con gráfico interactivo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Producción Semanal",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rendimiento diario (OT completadas)",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(12.dp))

                InteractiveLineChart(
                    values = listOf(30, 55, 70, 60, 80, 90, 75),
                    labels = listOf("L", "M", "X", "J", "V", "S", "D")
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
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
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 26.sp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8A8A8A), fontSize = 12.sp)
            )
        }
    }
}

@Composable
private fun InteractiveLineChart(values: List<Int>, labels: List<String>) {
    var selectedIndex by remember { mutableStateOf(-1) }

    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectTapGestures { offset ->
                        val widthPerItem = size.width / (values.size - 1)
                        val tappedIndex = (offset.x / widthPerItem).toInt().coerceIn(0, values.size - 1)
                        selectedIndex = tappedIndex
                    }
                }
        ) {
            val maxVal = (values.maxOrNull() ?: 1).toFloat()
            val widthPerItem = size.width / (values.size - 1)
            val path = Path()

            values.forEachIndexed { i, value ->
                val x = i * widthPerItem
                val y = size.height - (value / maxVal) * size.height
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(path, color = Color(0xFF366A9A), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f))

            // puntos
            values.forEachIndexed { i, value ->
                val x = i * widthPerItem
                val y = size.height - (value / maxVal) * size.height
                drawCircle(
                    color = if (i == selectedIndex) Color(0xFF1E3A8A) else Color(0xFF295FAB),
                    radius = if (i == selectedIndex) 10f else 6f,
                    center = Offset(x, y)
                )
            }

            // valor flotante
            if (selectedIndex >= 0) {
                val x = selectedIndex * widthPerItem
                val y = size.height - (values[selectedIndex] / maxVal) * size.height - 25f
                drawRoundRect(
                    color = Color(0xFF295FAB),
                    topLeft = Offset(x - 40f, y - 40f),
                    size = androidx.compose.ui.geometry.Size(80f, 35f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                )
            }
        }

        // etiquetas de días
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { i, label ->
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = if (i == selectedIndex) Color(0xFF295FAB) else Color.Gray
                )
            }
        }
    }
}

// Fecha en español
private fun getSpanishDate(): String {
    return try {
        val sdf = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
        val raw = sdf.format(Date())
        raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }
    } catch (e: Exception) {
        "Jueves, 4 de septiembre"
    }
}
