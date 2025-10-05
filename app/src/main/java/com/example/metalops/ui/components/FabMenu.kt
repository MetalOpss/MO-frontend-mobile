package com.example.metalops.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.ui.zIndex // <- importante

/**
 * FabMenu compatible con la llamada desde HomeScreen:
 * FabMenu(modifier = ..., onNotificacionesClick = {...}, onPerfilClick = {...}, onConfiguracionClick = {...})
 */
@Composable
fun FabMenu(
    modifier: Modifier = Modifier,
    onNotificacionesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    // contenedor para posicionar el FAB en la esquina (HomeScreen ya usa Modifier.align)
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.BottomEnd) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // botones pequeños (visible solo cuando expanded == true)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(150)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .zIndex(1f) // <- los mini fabs quedan por encima del FAB principal
                ) {
                    MiniFab(
                        onClick = {
                            expanded = false
                            println("CLICK: notificaciones")
                            onNotificacionesClick()
                        },
                        contentDescription = "Notificaciones",
                        icon = Icons.Default.Notifications
                    )
                    MiniFab(
                        onClick = {
                            expanded = false
                            println("CLICK: perfil")
                            onPerfilClick()
                        },
                        contentDescription = "Perfil",
                        icon = Icons.Default.Person
                    )
                    MiniFab(
                        onClick = {
                            expanded = false
                            println("CLICK: configuracion")
                            onConfiguracionClick()
                        },
                        contentDescription = "Configuración",
                        icon = Icons.Default.Settings
                    )
                }
            }

            // FAB principal
            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = MaterialTheme.colorScheme.primary,
                // opcional: si quieres que el FAB principal quede "debajo" lógicamente,
                // lo dejamos sin zIndex o con zIndex(0f)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = if (expanded) "Cerrar menú" else "Abrir menú",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun MiniFab(
    onClick: () -> Unit,
    contentDescription: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    // Tamaño pequeño para mini-FAB
    val size = 44.dp
    Surface(
        shape = CircleShape,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        // opcional: puedes darle color de fondo aquí si quieres
        // color = MaterialTheme.colorScheme.primary
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(size)
        ) {
            Icon(icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
