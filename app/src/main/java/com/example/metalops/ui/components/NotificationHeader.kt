package com.example.metalops.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationHeader(
    title: String = "Notificaciones",
    subtitle: String = "Buzón de notificaciones",
    navController: NavHostController? = null,
    onClose: () -> Unit = { navController?.popBackStack() }
) {
    TopAppBar(
        title = {
            Column {
                Text(text = title)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
            }
        },
        actions = {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    )
}
