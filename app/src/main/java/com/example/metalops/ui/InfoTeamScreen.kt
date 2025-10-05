package com.example.metalops.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.metalops.ui.components.CreditsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTeamScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { inner ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(inner)
            .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "MetalOps Team", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Información sobre nuestro equipo de desarrollo", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))

            // tarjeta versión (igual que mockup)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Versión")
                    Text(text = "Beta 1", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Créditos", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))

            // Ejemplo de tarjetas de miembros
            CreditsCard(
                name = "Persona 1",
                role = "Desarrollador",
                extra = "MetalOps Team"
            )

            Spacer(modifier = Modifier.height(12.dp))

            CreditsCard(
                name = "Persona 2",
                role = "Desarrollador",
                extra = "MetalOps Team"
            )
        }
    }
}
