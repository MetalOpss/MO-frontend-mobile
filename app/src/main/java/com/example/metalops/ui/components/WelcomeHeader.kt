package com.example.metalops.ui.components

import android.os.Build
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WelcomeHeader() {
    val dateText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDate = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
        dateFormatter.format(currentDate)
    } else {
        val sdf = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
        sdf.format(Date())
    }

    androidx.compose.foundation.layout.Column {
        Text(
            text = "¡Bienvenido, <Fullname>!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = dateText,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}