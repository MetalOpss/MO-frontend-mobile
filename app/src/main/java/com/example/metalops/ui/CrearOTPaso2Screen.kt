package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOTPaso2Screen(
    onSiguiente: () -> Unit,
    onAtras: () -> Unit,
    onCerrar: () -> Unit
) {
    val scrollState = rememberScrollState()

    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var tipoDoc by remember { mutableStateOf("Tipo de documento") }
    var nroDoc by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Aquí puedes poner acciones extra (menú, ayuda, etc.)
                },
                containerColor = Color(0xFF366A9A)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Crear nueva OT", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onCerrar) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Text(
                text = "Paso 2: Datos del cliente",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Datos personales
            Text("Datos personales", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                placeholder = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                placeholder = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            Spacer(Modifier.height(16.dp))

            // Contacto
            Text("Contacto", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            Spacer(Modifier.height(16.dp))

            // Identificación
            Text("Identificación", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(tipoDoc)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(text = { Text("DNI") }, onClick = {
                        tipoDoc = "DNI"
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("RUC") }, onClick = {
                        tipoDoc = "RUC"
                        expanded = false
                    })
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nroDoc,
                onValueChange = { nroDoc = it },
                placeholder = { Text("Número de documento") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            Spacer(Modifier.height(24.dp))

            // Botones Retroceder / Siguiente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onAtras,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Retroceder")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onSiguiente,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF366A9A)),
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Siguiente", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCrearOTPaso2Screen() {
    CrearOTPaso2Screen(
        onSiguiente = {},
        onAtras = {},
        onCerrar = {}
    )
}