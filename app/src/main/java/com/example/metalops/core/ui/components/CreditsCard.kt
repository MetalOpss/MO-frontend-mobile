package com.example.metalops.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CreditsCard(
    name: String,
    role: String,
    extra: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = role, color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = extra, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                IconButton(onClick = { println("Social clicked for $name (left)") }) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Social")
                }
                IconButton(onClick = { println("Social clicked for $name (right)") }) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Social")
                }
            }
        }
    }
}
