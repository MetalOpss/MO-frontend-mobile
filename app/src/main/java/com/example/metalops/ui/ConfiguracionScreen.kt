package com.example.metalops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.metalops.ui.components.SettingsRow

@Composable
fun ConfiguracionScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Información",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Text(
            text = "Información sobre MetalOps",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp, top = 4.dp)
        )

        // Mantengo las filas que tenías; ahora navegan a rutas registradas
        SettingsRow(
            title = "MetalOps Build",
            subtitle = "Información",
            onClick = {
                println("Configuracion: MetalOps Build clicked")
                navController.navigate("info_build")
            }
        )
        Divider(color = Color.LightGray.copy(alpha = 0.3f))
        SettingsRow(
            title = "MetalOps Team",
            subtitle = "Información",
            onClick = {
                println("Configuracion: MetalOps Team clicked")
                navController.navigate("info_team")
            }
        )
        Divider(color = Color.LightGray.copy(alpha = 0.3f))
        SettingsRow(
            title = "Contacto",
            subtitle = "Información",
            onClick = {
                println("Configuracion: Contacto clicked")
                navController.navigate("contacto")
            }
        )
    }
}
