package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDropDown
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
fun FormDropdownField(
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = { /* readOnly field */ },
            readOnly = true,
            placeholder = { if (selected.isEmpty()) Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray.copy(alpha = 0.6f),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
            )
        )

        // Opciones ejemplo — cámbialas según tus datos reales
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(text = { Text("Opción 1") }, onClick = {
                selected = "Opción 1"; expanded = false
            })
            DropdownMenuItem(text = { Text("Opción 2") }, onClick = {
                selected = "Opción 2"; expanded = false
            })
            DropdownMenuItem(text = { Text("Opción 3") }, onClick = {
                selected = "Opción 3"; expanded = false
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOTPaso3Screen(
    onSiguiente: () -> Unit = {},
    onAtras: () -> Unit = {},
    onCerrar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header (mismo tamaño que Paso 2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Crear nueva OT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Paso 3: Servicios de la OT",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onCerrar,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contenido — dropdowns (usa la función incluida arriba)
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FormDropdownField(label = "Servicios")
            FormDropdownField(label = "Servicios")
            FormDropdownField(label = "Servicios")
            // agrega o quita campos según necesites
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones inferiores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onAtras,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Atrás", color = Color.Gray)
            }

            Button(
                onClick = onSiguiente,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF366A9A)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Siguiente", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewCrearOTPaso3Screen() {
    CrearOTPaso3Screen()
}
