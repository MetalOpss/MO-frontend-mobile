package com.example.metalops.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.metalops.R

@Composable
fun FabMenu(
    modifier: Modifier = Modifier,
    onNotificacionesClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onConfiguracionClick: () -> Unit
) {
    val primaryBlue = Color(0xFF1976D2)
    val lightBlue = primaryBlue.copy(alpha = 0.08f)

    var expanded by remember { mutableStateOf(false) }

    // Pequeña animación de “pop” para el FAB principal
    val mainScale by animateFloatAsState(
        targetValue = if (expanded) 1.05f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "fabScale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botones que aparecen arriba del FAB principal
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(200)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(200)
                        ),
                exit = fadeOut(animationSpec = tween(150)) +
                        slideOutVertically(
                            targetOffsetY = { it / 2 },
                            animationSpec = tween(150)
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Notificaciones
                    SmallFloatingActionButton(
                        onClick = {
                            expanded = false
                            onNotificacionesClick()
                        },
                        containerColor = Color.White,
                        contentColor = primaryBlue,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones"
                        )
                    }

                    // Perfil
                    SmallFloatingActionButton(
                        onClick = {
                            expanded = false
                            onPerfilClick()
                        },
                        containerColor = Color.White,
                        contentColor = primaryBlue,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    }

                    // Configuración
                    SmallFloatingActionButton(
                        onClick = {
                            expanded = false
                            onConfiguracionClick()
                        },
                        containerColor = Color.White,
                        contentColor = primaryBlue,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                }
            }

            // FAB principal con el logo de MetalOps
            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = primaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 10.dp
                ),
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(18.dp))
                    .scale(mainScale)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo_metalops),
                    contentDescription = "MetalOps menu",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
