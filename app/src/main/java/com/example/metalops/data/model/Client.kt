package com.example.metalops.data.model

data class Client(
    val id: String = "",
    val name: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val document: String = "",     // DNI / RUC
    val agentId: String = ""       // UID del usuario que lo registró
)
