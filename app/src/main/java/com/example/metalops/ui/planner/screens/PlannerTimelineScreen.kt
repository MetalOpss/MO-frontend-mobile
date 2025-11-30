package com.example.metalops.ui.planner.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.remote.PlannerStepsRepository
import com.example.metalops.data.remote.PlannerTimelineItem

@Composable
fun PlannerTimelineScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repo = remember { PlannerStepsRepository() }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var items by remember { mutableStateOf<List<PlannerTimelineItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            items = repo.getRecentTimelineItems()
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = e.message ?: "Error al cargar línea de tiempo"
        } finally {
            isLoading = false
        }
    }

    val primaryBlue = Color(0xFF1976D2)

    // Productividad por hora (muy simple, basado en timestamp "YYYY-MM-DD HH:MM")
    val hours = (6..22 step 2).toList() // 6,8,10,...,22
    val countsByHour = remember(items) {
        val map = mutableMapOf<Int, Int>()
        hours.forEach { map[it] = 0 }

        items.forEach { item ->
            val ts = item.timestamp
            if (ts.length >= 16) {
                // asumiendo formato "YYYY-MM-DD HH:MM"
                val hourStr = ts.substring(11, 13)
                val hour = hourStr.toIntOrNull()
                if (hour != null) {
                    val key = hours.minByOrNull { kotlin.math.abs(it - hour) } ?: hour
                    map[key] = (map[key] ?: 0) + 1
                }
            }
        }
        map
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Encabezado global de MetalOps
            AppHeader()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Línea de tiempo",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = primaryBlue
            )
            Text(
                text = "Seguimiento de servicios en curso y finalizados.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- Mini gráfico por hora ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Distribución por hora (hoy)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxCount = (countsByHour.values.maxOrNull() ?: 1).coerceAtLeast(1)
                        hours.forEach { hour ->
                            val hCount = countsByHour[hour] ?: 0
                            val ratio = hCount.toFloat() / maxCount.toFloat()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .height(60.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val barHeight = size.height * ratio
                                        drawLine(
                                            color = primaryBlue,
                                            start = androidx.compose.ui.geometry.Offset(
                                                x = size.width / 2f,
                                                y = size.height
                                            ),
                                            end = androidx.compose.ui.geometry.Offset(
                                                x = size.width / 2f,
                                                y = size.height - barHeight
                                            ),
                                            strokeWidth = size.width,
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format("%02d", hour),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Eventos recientes",
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
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (items.isEmpty() && !isLoading && errorMessage == null) {
                Text(
                    text = "Aún no hay eventos en la línea de tiempo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item ->
                        TimelineEventCard(
                            item = item,
                            primaryBlue = primaryBlue
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
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun TimelineEventCard(
    item: PlannerTimelineItem,
    primaryBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.serviceType,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = extractHour(item.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryBlue
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Operario: ${item.operarioName}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Estado: ${item.status}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            if (item.workOrderCode != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "OT: ${item.workOrderCode}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun extractHour(ts: String): String {
    return if (ts.length >= 16) {
        // "YYYY-MM-DD HH:MM"
        ts.substring(11, 16)
    } else {
        ts
    }
}