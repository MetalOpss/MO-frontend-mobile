package com.example.metalops.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun NotificationTabs(
    selected: TabSelection,
    onSelect: (TabSelection) -> Unit
) {
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        Button(
            onClick = { onSelect(TabSelection.READ) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected == TabSelection.READ) Color(0xFF295FAB) else Color(0xFFF0F0F0),
                contentColor = if (selected == TabSelection.READ) Color.White else Color.Black
            ),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("Leídas")
        }

        Button(
            onClick = { onSelect(TabSelection.UNREAD) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected == TabSelection.UNREAD) Color(0xFF295FAB) else Color(0xFFF0F0F0),
                contentColor = if (selected == TabSelection.UNREAD) Color.White else Color.Black
            )
        ) {
            Text("No leídas")
        }
    }
}

enum class TabSelection { READ, UNREAD }
