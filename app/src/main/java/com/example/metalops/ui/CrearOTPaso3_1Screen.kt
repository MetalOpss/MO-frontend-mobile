package com.example.metalops.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CrearOTPaso3_1Screen(
    onAtras: () -> Unit = {},
    onSiguiente: () -> Unit = {},
    onCerrar: () -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 🔹 Título
        Text(
            text = "Crear nueva OT",
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 24.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Subtítulo
        Text(
            text = "Paso 3.1: Servicios de la OT",
            style = MaterialTheme.typography.bodyMedium, // 👈 estilo sutil como en la captura
            fontSize = 20.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Dropdowns con iconos
        ServicioDropdown("Diseñado", listOf("Sí", "No", "Pendiente"), Icons.Filled.Build)
        ServicioDropdown("Cortado", listOf("Sí", "No", "Pendiente"), Icons.Filled.ContentCut)
        ServicioDropdown("Plegado", listOf("Sí", "No", "Pendiente"), Icons.Filled.ViewWeek)
        ServicioDropdown("Soldado", listOf("Sí", "No", "Pendiente"), Icons.Filled.Handyman)
        ServicioDropdown("Grabado", listOf("Sí", "No", "Pendiente"), Icons.Filled.Create)

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onAtras,
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Atrás", fontSize = 16.sp)
            }

            Button(
                onClick = onSiguiente,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Siguiente", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioDropdown(label: String, options: List<String>, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.first()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = "$label: $selectedOption",
            onValueChange = {},
            readOnly = true,
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF1976D2)) }, // 🔹 Icono
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            },
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp), // 🔹 Texto más grande
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedBorderColor = Color.Gray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 16.sp) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}


