package com.example.metalops.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class NotificationModel(
    val id: Int,
    val title: String,
    val body: String,
    val read: Boolean = false
)

@Composable
fun NotificationItem(texto: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, modifier = Modifier.padding(16.dp), color = Color.Black)
    }
}

@Composable
fun NotificationItemDetailed(
    item: NotificationModel,
    modifier: Modifier = Modifier,
    onToggleRead: (Int) -> Unit = {},
    onOpen: (Int) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (item.read) Color(0xFFEFEFEF) else Color(0xFFE3F2FD),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (item.read) Color.Gray else Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.body, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = { onOpen(item.id) }) {
                Icon(imageVector = Icons.Default.OpenInFull, contentDescription = "Abrir")
            }
        }
    }
}
