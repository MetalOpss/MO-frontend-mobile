package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ClientesScreen(navController: NavHostController) {
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
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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

        // Botón Registrar Cliente
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate("registrar_cliente") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF295FAB)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Registrar", tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Registrar Cliente", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido scrolleable
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

            // Lista de clientes
            items(10) { index ->
                ClientCard(
                    nombre = "Nombre del Cliente $index",
                    dni = "DNI/RUC $index",
                    onEditClick = { navController.navigate("editar_cliente") }
                )
            }
        }
    }
}

// Tarjeta informe
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
            Text(value, fontSize = 22.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
        }
    }
}

// Card de cliente con botón de editar
@Composable
fun ClientCard(nombre: String, dni: String, onEditClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(nombre, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                Text(dni, fontSize = 14.sp, color = Color.Gray)
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Cliente")
            }
        }
    }
}
