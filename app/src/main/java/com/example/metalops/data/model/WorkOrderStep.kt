package com.example.metalops.data.model

data class WorkOrderStep(
    val id: String = "",
    val index: Int = 0,
    val serviceType: String = "",
    val operarioId: String = "",
    val operarioName: String = "",
    val machine: String = "",
    val estimatedStart: String = "",
    val estimatedEnd: String = "",
    val status: String = "bloqueada", // bloqueada, liberada, en curso, finalizada
    val completedAt: String = ""
)