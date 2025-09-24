package com.example.metalops.ui

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background

@Composable
fun CrearOTPaso5Screen(
    onAtras: () -> Unit,
    onFinalizar: () -> Unit,
    onCerrar: () -> Unit
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Launcher para abrir selector de archivos
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Encabezado
        Text(
            text = "Crear nueva OT",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Paso 5: Adjuntar archivo",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Bolitas de progreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .border(
                            width = 2.dp,
                            color = if (index == 4) Color(0xFF366A9A) else Color.Gray,
                            shape = CircleShape
                        )
                        .padding(2.dp)
                        .background(
                            color = if (index <= 4) Color(0xFF366A9A) else Color.Transparent,
                            shape = CircleShape
                        )
                )
                if (index < 4) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Caja para cargar archivo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .clickable { filePickerLauncher.launch("*/*") }, // Abre el picker
            contentAlignment = Alignment.Center
        ) {
            if (selectedFileUri != null) {
                Text(
                    text = "Archivo seleccionado:\n${selectedFileUri.toString()}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📎", fontSize = 64.sp) // ícono de adjuntar
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Carga tu archivo", fontSize = 16.sp)
                    Text("Paso opcional", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones inferiores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onAtras,
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Retroceder", color = Color.Black)
            }

            Button(
                onClick = { showDialog = true }, // ahora abre modal
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF366A9A))
            ) {
                Text("Finalizar", color = Color.White)
            }
        }
    }

    // Modal de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onFinalizar() // volver a OT o flujo principal
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF366A9A))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // ✅ Círculo con el check
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(1.dp, Color(0xFF90CAF9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✔", fontSize = 28.sp, color = Color(0xFF366A9A))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("OT registrada", fontSize = 16.sp, color = Color.Black)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
