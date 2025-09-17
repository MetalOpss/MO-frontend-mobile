package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClientesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Título fijo
        Text(
            text = "Clientes",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Buscador fijo
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Buscar Cliente") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botones fijos (registrar / editar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /* Registrar Cliente */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF295FAB)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Registrar", tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Registrar Cliente", color = Color.White)
            }

            Button(
                onClick = { /* Editar Cliente */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Editar Cliente", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido scrolleable (totales, filtros, lista)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Tarjetas Totales centradas
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InfoCard("Totales", "100")
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoCard("Activos", "50")
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoCard("Inactivos", "20")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Filtros (2 filas de 2 filtros con weight para repartir)
            item {
                Text("Filtros", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DropdownFilter("Sección", modifier = Modifier.weight(1f))
                    DropdownFilter("Sección", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DropdownFilter("Sección", modifier = Modifier.weight(1f))
                    DropdownFilter("Sección", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lista de clientes
            items(10) { index ->
                ClientCard("Nombre del Cliente $index", "DNI/RUC $index")
            }
        }
    }
}

// Tarjeta informe (Totales, Activos, Inactivos). Ancho controlado para que quede centrada.
@Composable
fun InfoCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 16.sp, color = Color.Black)
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

// DropdownFilter ahora recibe un modifier — usa ese modifier para aplicar weight desde el caller.
@Composable
fun DropdownFilter(label: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(label) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth() // usa el width que le dé el parent (p. ej. weight)
        ) {
            Text(selectedOption)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Opción 1") }, onClick = {
                selectedOption = "Opción 1"
                expanded = false
            })
            DropdownMenuItem(text = { Text("Opción 2") }, onClick = {
                selectedOption = "Opción 2"
                expanded = false
            })
        }
    }
}

// Card de cliente simple con botón de más opciones
@Composable
fun ClientCard(nombre: String, dni: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(dni, fontSize = 14.sp, color = Color.Gray)
            }

            IconButton(onClick = { /* Más opciones */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
            }
        }
    }
}
