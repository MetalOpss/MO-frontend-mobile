package com.example.metalops.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOTPaso1Screen(
    onSiguiente: () -> Unit,
    onCerrar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val maxChars = 500

    var expanded by remember { mutableStateOf(false) }
    var localidadSeleccionada by remember { mutableStateOf("Seleccionar localidad") }
    val localidades = listOf("Local A", "Local B", "Local C")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header con título y botón cerrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Crear nueva OT",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onCerrar) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Text(
            "Paso 1: Datos generales de la OT",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo descripción más grande con contador
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = descripcion,
                onValueChange = { if (it.length <= maxChars) descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                maxLines = 5
            )
            Text(
                text = "${maxChars - descripcion.length}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Localidad (Dropdown)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = localidadSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Localidad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                localidades.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            localidadSeleccionada = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón siguiente
        Button(
            onClick = onSiguiente,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF366A9A),
                contentColor = Color.White
            )
        ) {
            Text("Siguiente")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCrearOTPaso1Screen() {
    CrearOTPaso1Screen(
        onSiguiente = {},
        onCerrar = {}
    )
}
