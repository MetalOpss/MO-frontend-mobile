package com.example.metalops.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * FabMenu reutilizable (Agente / Planner)
 * - FAB morado principal (cuadradito redondeado)
 * - 3 burbujas flotantes lilas con íconos morados
 */
@Composable
fun FabMenu(
    modifier: Modifier = Modifier,
    onNotificacionesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 🔹 Burbujas pequeñas
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.zIndex(1f)
                ) {
                    MiniFabBubble(
                        onClick = {
                            expanded = false
                            onNotificacionesClick()
                        },
                        icon = Icons.Default.Notifications,
                        contentDescription = "Notificaciones"
                    )
                    MiniFabBubble(
                        onClick = {
                            expanded = false
                            onPerfilClick()
                        },
                        icon = Icons.Default.Person,
                        contentDescription = "Perfil"
                    )
                    MiniFabBubble(
                        onClick = {
                            expanded = false
                            onConfiguracionClick()
                        },
                        icon = Icons.Default.Settings,
                        contentDescription = "Configuración"
                    )
                }
            }

            // 🔸 FAB principal morado
            MainActionFab(
                isExpanded = expanded,
                onClick = { expanded = !expanded }
            )
        }
    }
}

@Composable
private fun MiniFabBubble(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    val bubbleBg = Color(0xFFEDE5FF) // lila claro
    val iconTint = Color(0xFF5B3FA3) // morado oscuro visible

    // ✅ Uso de IconButton para click real, envuelto en Surface redondeada
    Surface(
        modifier = Modifier
            .size(48.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                clip = false
            ),
        shape = CircleShape,
        color = bubbleBg,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint
            )
        }
    }
}

@Composable
private fun MainActionFab(
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val mainBg = Color(0xFF5B3FA3) // morado del botón principal
    val iconTint = Color.White

    FloatingActionButton(
        onClick = onClick,
        containerColor = mainBg,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(width = 56.dp, height = 56.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = if (isExpanded) "Cerrar menú" else "Abrir menú",
            tint = iconTint
        )
    }
}
