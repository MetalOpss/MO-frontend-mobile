package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ------------------- DATA -------------------
data class OrdenTrabajo(
    val nombre: String,
    val fecha: String,
    val hora: String,
    val estado: EstadoOT
)

enum class EstadoOT(val displayName: String, val color: Color) {
    EN_REGISTRO("En registro", Color(0xFFE3F2FD)),
    PENDIENTE_SIN_DISEÑO("Pendiente sin diseño", Color(0xFFFFF3E0)),
    EN_EJECUCION("En ejecución", Color(0xFFE8F5E8)),
    POR_CORREGIR("Por corregir", Color(0xFFFFEBEE))
}

// ------------------- MAIN SCREEN -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdenesTrabajoScreen(navController: NavController) {
    val ordenesEjemplo = listOf(
        OrdenTrabajo("Nombre de la OT", "11 Sep, 8:00 AM", "", EstadoOT.EN_REGISTRO),
        OrdenTrabajo("Nombre de la OT", "11 Sep, 8:00 AM", "", EstadoOT.PENDIENTE_SIN_DISEÑO),
        OrdenTrabajo("Nombre de la OT", "11 Sep, 8:00 AM", "", EstadoOT.EN_EJECUCION)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Órdenes de Trabajo",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search + Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Field
            OutlinedTextField(
                value = "",
                onValueChange = { },
                placeholder = { Text("Buscar OT", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            // Register Button -> navegar
            Button(
                onClick = { navController.navigate("crear_ot_paso1") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Registrar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar OT", color = Color.White, fontSize = 14.sp)
            }
        }

        // Status Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusCard("En registro", "100", Modifier.weight(1f))
            StatusCard("Sin diseño", "50", Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusCard("En ejecución", "20", Modifier.weight(1f))
            StatusCard("Por corregir", "2", Modifier.weight(1f))
        }

        // Filters
        Text(
            text = "Filtros",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterDropdown("Sección", Modifier.weight(1f))
            FilterDropdown("Sección", Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterDropdown("Sección", Modifier.weight(1f))
            FilterDropdown("Sección", Modifier.weight(1f))
        }

        // Orders List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ordenesEjemplo) { orden ->
                OrdenTrabajoCard(orden = orden)
            }
        }
    }
}

// ------------------- SUB-COMPONENTS -------------------
@Composable
fun StatusCard(title: String, count: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(text: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.Gray)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Opción 1") }, onClick = { expanded = false })
            DropdownMenuItem(text = { Text("Opción 2") }, onClick = { expanded = false })
        }
    }
}

@Composable
fun OrdenTrabajoCard(orden: OrdenTrabajo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(orden.nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(orden.fecha, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.background(
                        color = orden.estado.color,
                        shape = RoundedCornerShape(16.dp)
                    ).padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        orden.estado.displayName,
                        fontSize = 12.sp,
                        color = when (orden.estado) {
                            EstadoOT.EN_REGISTRO -> Color(0xFF1976D2)
                            EstadoOT.PENDIENTE_SIN_DISEÑO -> Color(0xFFE65100)
                            EstadoOT.EN_EJECUCION -> Color(0xFF2E7D32)
                            EstadoOT.POR_CORREGIR -> Color(0xFFC62828)
                        }
                    )
                }
            }
        }
    }
}
