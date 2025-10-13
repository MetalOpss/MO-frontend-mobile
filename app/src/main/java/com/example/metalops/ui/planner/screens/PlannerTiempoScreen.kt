package com.example.metalops.ui.planner.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTiempoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Línea de Tiempo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF295FAB),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // ✅ hace scroll si hay muchos eventos
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                "Eventos Recientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF295FAB)
            )

            Spacer(modifier = Modifier.height(20.dp))

            val timelineEvents = listOf(
                TimelineEvent("Inicio de Turno", "08:00 AM", "El equipo inició labores en Planta A", true),
                TimelineEvent("Mantenimiento", "09:30 AM", "Revisión de motor principal", false),
                TimelineEvent("Pausa programada", "11:00 AM", "Descanso de 15 minutos", false),
                TimelineEvent("Producción", "12:00 PM", "Inicio del lote #42", true),
                TimelineEvent("Reporte de avance", "02:00 PM", "Enviado al sistema ERP", true),
                TimelineEvent("Fin de Turno", "05:00 PM", "Turno completado sin incidencias", false)
            )

            TimelineList(events = timelineEvents)
        }
    }
}

data class TimelineEvent(
    val title: String,
    val time: String,
    val description: String,
    val completed: Boolean
)

@Composable
fun TimelineList(events: List<TimelineEvent>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        events.forEachIndexed { index, event ->
            TimelineItem(event, isLast = index == events.lastIndex)
        }
    }
}

@Composable
fun TimelineItem(event: TimelineEvent, isLast: Boolean) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        // ✅ Indicador de línea
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        ) {
            Canvas(modifier = Modifier.size(18.dp)) {
                drawCircle(
                    color = if (event.completed) Color(0xFF295FAB) else Color.Gray,
                    radius = size.minDimension / 2
                )
            }

            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .height(80.dp) // ✅ fija la altura de la línea entre eventos
                        .padding(top = 4.dp)
                ) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // ✅ Contenedor del evento
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(event.title, fontWeight = FontWeight.Bold)
                    Text(event.time, color = Color(0xFF295FAB), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(event.description, color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}
