package com.example.metalops.core.ui.components

data class Task(
    val time: String,
    val name: String,
    val description: String,
    val status: String = "Pendiente"
)