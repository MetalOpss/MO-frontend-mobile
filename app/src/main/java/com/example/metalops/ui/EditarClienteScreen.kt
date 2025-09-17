package com.example.metalops.ui

import androidx.compose.runtime.Composable

@Composable
fun EditarClienteScreen(onClose: () -> Unit, onAction: () -> Unit) {
    RegistrarClienteScreen(
        onClose = onClose,
        onAction = onAction,
        isEditing = true
    )
}