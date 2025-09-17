package com.yourpackage.ui.modals
//error del paquete falta .modals
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class FormStep(
    val number: Int,
    val title: String,
    val icon: ImageVector,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalFormSteps(
    onDismiss: () -> Unit = {},
    currentStep: Int = 1
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        ModalFormContent(
            onDismiss = onDismiss,
            currentStep = currentStep
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdownField(
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Opción 1", "Opción 2", "Opción 3").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedText = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StepItem(
    step: FormStep,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                step.isCompleted -> Color(0xFF1976D2).copy(alpha = 0.1f)
                step.isActive -> Color(0xFF1976D2).copy(alpha = 0.1f)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Step number circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            step.isCompleted -> Color(0xFF1976D2)
                            step.isActive -> Color(0xFF1976D2)
                            else -> Color.Gray.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completado",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = step.number.toString(),
                        color = if (step.isActive) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Step title
            if (step.title.isNotEmpty()) {
                Text(
                    text = step.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        step.isCompleted || step.isActive -> Color(0xFF1976D2)
                        else -> Color.Gray
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Step icon
            Icon(
                step.icon,
                contentDescription = step.title,
                tint = when {
                    step.isCompleted || step.isActive -> Color(0xFF1976D2)
                    else -> Color.Gray
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ModalFormContent(
    onDismiss: () -> Unit = {},
    currentStep: Int = 1
) {
    val steps = listOf(
        FormStep(1, "Preparado", Icons.Default.Info, isCompleted = true),
        FormStep(2, "", Icons.Default.Build, isActive = currentStep == 2),
        FormStep(3, "Plegado", Icons.Default.ViewModule, isActive = currentStep == 3),
        FormStep(4, "Soldado", Icons.Default.Construction, isActive = currentStep == 4),
        FormStep(5, "Grabado", Icons.Default.Create, isActive = currentStep == 5)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Paso 3: Servicios de la OT",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormDropdownField("Expediente")
                FormDropdownField("Cortado")
                FormDropdownField("Plegado")
                FormDropdownField("Soldado")
                FormDropdownField("Grabado")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Steps
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                steps.forEach { step ->
                    StepItem(step = step)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Retroceder",
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Siguiente",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModalFormStepsPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            ModalFormContent()
        }
    }
}