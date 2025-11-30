package com.example.metalops.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Sección que se está mostrando en el modal
 */
enum class ProfileModalSection {
    PERFIL,
    NOTIFICACIONES,
    CONFIGURACION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileActionsModal(
    section: ProfileModalSection,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        when (section) {
            ProfileModalSection.PERFIL -> PerfilContent()
            ProfileModalSection.NOTIFICACIONES -> NotificacionesContent()
            ProfileModalSection.CONFIGURACION -> ConfiguracionContent()
        }
    }
}

@Composable
private fun ColumnScope.PerfilContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Nombre: Usuario MetalOps",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Rol: Planner / Operario",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Correo: usuario@metalops.com",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE0E0E0))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aquí luego puedes conectar los datos reales del usuario.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ColumnScope.NotificacionesContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notificaciones",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Notificaciones",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Por ahora no tienes notificaciones pendientes.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE0E0E0))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Más adelante puedes listar alertas de OT, tareas, turnos, etc.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ColumnScope.ConfiguracionContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Configuración",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Aquí podrás ajustar preferencias del rol, tema, idioma, etc.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE0E0E0))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Puedes reemplazar este contenido por switches, toggles, etc.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
