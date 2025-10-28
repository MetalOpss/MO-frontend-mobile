package com.example.metalops.ui.operario.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// --- modelos simples para mock/demo ---
data class Operario(
    val nombre: String,
    val dni: String,
    val estado: EstadoOperario,
    val otAsignada: String?
)

enum class EstadoOperario {
    OCUPADO,
    DISPONIBLE,
    EN_PAUSA
}

@Composable
fun OperariosScreen(
    navController: NavHostController? = null // nav opcional
) {
    var search by remember { mutableStateOf("") }

    // datos mock
    val ocupadosCount = 2
    val disponiblesCount = 1

    val operarios = listOf(
        Operario(
            nombre = "Carlos Pérez",
            dni = "74859632",
            estado = EstadoOperario.OCUPADO,
            otAsignada = "OT-00123"
        ),
        Operario(
            nombre = "María López",
            dni = "70895641",
            estado = EstadoOperario.DISPONIBLE,
            otAsignada = null
        ),
        Operario(
            nombre = "Luis García",
            dni = "75102468",
            estado = EstadoOperario.EN_PAUSA,
            otAsignada = "OT-00456"
        ),
        Operario(
            nombre = "Andrea Ramos",
            dni = "73820194",
            estado = EstadoOperario.OCUPADO,
            otAsignada = "OT-00311"
        )
    ).filter {
        it.nombre.contains(search, ignoreCase = true) ||
                it.dni.contains(search, ignoreCase = true)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {

            // Título
            Text(
                text = "Operarios",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Row métricas y buscador
            MetricsAndSearchRow(
                ocupados = ocupadosCount,
                disponibles = disponiblesCount,
                search = search,
                onSearchChange = { search = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista scrollable
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(operarios) { op ->
                    OperarioCard(operario = op)
                }
            }
        }
    }
}

@Composable
private fun MetricsAndSearchRow(
    ocupados: Int,
    disponibles: Int,
    search: String,
    onSearchChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Operarios Ocupados",
                value = ocupados.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Operarios Disponibles",
                value = disponibles.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SearchField(
            value = search,
            onValueChange = onSearchChange
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = CardDefaults.outlinedCardBorder(true).copy(
            brush = SolidColor(Color(0xFFD9D9D9))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF6E6E6E)
            )
        },
        placeholder = {
            Text(
                text = "Buscar operario",
                fontSize = 14.sp,
                color = Color(0xFF9C9C9C)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F6F8),
            focusedContainerColor = Color(0xFFF5F6F8),
            disabledContainerColor = Color(0xFFF5F6F8),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            cursorColor = Color.Black
        )
    )
}

@Composable
private fun OperarioCard(
    operario: Operario
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = operario.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "DNI: ${operario.dni}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF4A4A4A)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EstadoChip(estado = operario.estado)

                Row {
                    Text(
                        text = "OT asignada: ",
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A4A)
                    )
                    Text(
                        text = operario.otAsignada ?: "-",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoChip(estado: EstadoOperario) {
    val (bgColor, textColor, label) = when (estado) {
        EstadoOperario.OCUPADO -> Triple(
            Color(0xFFDDE7F7),
            Color(0xFF1F3F72),
            "Ocupado"
        )
        EstadoOperario.DISPONIBLE -> Triple(
            Color(0xFFE8F8DF),
            Color(0xFF2E6B1C),
            "Disponible"
        )
        EstadoOperario.EN_PAUSA -> Triple(
            Color(0xFFFBF3CC),
            Color(0xFF7A6724),
            "En pausa"
        )
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
