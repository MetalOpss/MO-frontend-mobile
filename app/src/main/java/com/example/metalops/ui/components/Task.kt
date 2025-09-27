package com.example.metalops.ui.components

data class Task(
    val time: String,
    val name: String,
    val description: String,
    val status: String = "Pendiente"
)