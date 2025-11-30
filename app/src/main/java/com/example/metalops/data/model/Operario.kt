package com.example.metalops.data.model

/**
 * Modelo base para un operario.
 * Se guarda en la colección "operarios" de Firestore.
 */
data class Operario(
    val id: String = "",              // id del documento en Firestore
    val nombre: String = "",          // nombre completo del operario
    val dni: String = "",             // documento de identidad
    val estado: String = "disponible" // disponible / ocupado / en_pausa
)
