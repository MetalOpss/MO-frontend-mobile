package com.example.metalops.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.shape.CircleShape

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOTPaso4Screen(
    onSiguiente: () -> Unit = {},
    onAtras: () -> Unit = {},
    onCerrar: () -> Unit = {},
    onOpciones: () -> Unit = {} //  para la bolita
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    var hora by remember { mutableStateOf("") }
    var cotizacion by remember { mutableStateOf("") }

    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Crear nueva OT",
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Paso 4: Datos adicionales",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = onCerrar, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close, // 👉 puse Close en lugar de flecha
                        contentDescription = "Cerrar",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar { it.uppercaseChar() } + " ${currentMonth.year}",
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ArrowBackIos, contentDescription = "Mes anterior")
                    }
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = "Mes siguiente")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Días de la semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val days = listOf("D","L","M","X","J","V","S")
                for (d in days) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = d, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Días del mes
            val weeks = remember(currentMonth) { buildMonthCalendar(currentMonth) }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (week in weeks) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (day in week) {
                            DayCell(
                                date = day,
                                isSelected = day != null && day == selectedDate,
                                isInMonth = day?.month == currentMonth.month,
                                onClick = { d -> if (d != null) selectedDate = d }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de formulario
            OutlinedTextField(
                value = selectedDate?.toString() ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Fecha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("08:00 AM") }
            )

            OutlinedTextField(
                value = cotizacion,
                onValueChange = { cotizacion = it },
                label = { Text("Cotización") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("S/ 0.00") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAtras,
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text("Retroceder", fontSize = 16.sp)
                }

                Button(
                    onClick = { if (selectedDate != null) onSiguiente() },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF366A9A)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Siguiente", fontSize = 16.sp, color = Color.White)
                }
            }
        }

        // 👉 Bolita flotante arriba a la derecha
        FloatingActionButton(
            onClick = onOpciones,
            shape = CircleShape,
            containerColor = Color(0xFF366A9A),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 56.dp, end = 8.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones",
                tint = Color.White
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RowScope.DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    isInMonth: Boolean,
    onClick: (LocalDate?) -> Unit
) {
    val bg = when {
        date == null -> Color.Transparent
        isSelected -> Color(0xFF1976D2)
        else -> if (isInMonth) Color(0xFFF2F4F6) else Color(0xFFF8F8F8)
    }

    val contentColor = when {
        date == null -> Color.Transparent
        isSelected -> Color.White
        else -> if (isInMonth) Color.Black else Color.Gray
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable { if (date != null) onClick(date) },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                color = contentColor,
                fontSize = 14.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun buildMonthCalendar(month: YearMonth): List<List<LocalDate?>> {
    val firstOfMonth = month.atDay(1)
    val dayOfWeekOfFirst = firstOfMonth.dayOfWeek.value % 7 // domingo=0
    val start = firstOfMonth.minusDays(dayOfWeekOfFirst.toLong())

    val weeks = mutableListOf<List<LocalDate?>>()
    var cur = start
    repeat(6) {
        val week = (0 until 7).map { idx -> cur.plusDays(idx.toLong()) }
        weeks.add(week)
        cur = cur.plusDays(7)
    }
    return weeks
}