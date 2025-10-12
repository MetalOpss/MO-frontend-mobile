package com.example.metalops.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun AvatarHeader(
    avatarPainter: Painter? = null,
    fullName: String,
    modifier: Modifier = Modifier
) {
    // fallback: si no hay painter, usamos el vector Person
    val imgPainter = avatarPainter ?: rememberVectorPainter(Icons.Default.Person)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = imgPainter,
            contentDescription = "Avatar de $fullName",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(listOf(Color.LightGray.copy(alpha = 0.2f), Color.LightGray)),
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = fullName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}
