package com.example.metalops.ui.planner.screens

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

@Composable
fun PlannerOTsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Órdenes de trabajo",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar OT") },
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Estadísticas principales (fila 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Totales", "35", highlighted = true, modifier = Modifier.weight(1f))
            StatCard("En curso", "5", highlighted = true, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Estadísticas principales (fila 2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Completadas", "23", modifier = Modifier.weight(1f))
            StatCard("Canceladas", "7", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Filtros", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip("Estado")
            FilterChip("Prioridad")
            FilterChip("Máquina")
        }

        Spacer(modifier = Modifier.height(20.dp))

        OTItem("OT-001", "Acabado")
        Spacer(modifier = Modifier.height(10.dp))
        OTItem("OT-002", "Soldadura")
    }
}

/**
 * StatCard ahora acepta un modifier para que el caller (Row) pueda pasar Modifier.weight(...)
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp), // height lo manejamos aquí junto al modifier que venga del caller
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) Color(0xFF295FAB) else Color(0xFFF2F2F2)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, color = if (highlighted) Color.White else Color.Black)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = if (highlighted) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun FilterChip(label: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF4F4F4), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = Color.Black, fontSize = 13.sp)
    }
}

@Composable
fun OTItem(codigo: String, proceso: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(codigo, fontWeight = FontWeight.Bold)
            Text(proceso, color = Color.Gray)
        }
    }
}
