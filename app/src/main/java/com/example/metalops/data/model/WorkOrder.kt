package com.example.metalops.data.model

data class WorkOrder(
    val id: String = "",              // id del documento en Firestore
    val code: String = "",            // OT-001, OT-002 (opcional, si no hay usamos id)

    val title: String = "",           // título o descripción corta
    val clientName: String = "",      // nombre del cliente
    val location: String = "",        // planta / local

    val priority: String = "media",   // alta / media / baja
    val status: String = "en registro", // en registro / en progreso / por corregir / en ejecución / completada / cancelada
    val type: String = "normal",      // normal / urgente / correccion

    val scheduledDate: String = "",   // 2025-11-29
    val scheduledTime: String = "",   // 08:00

    val isUrgent: Boolean = false,    // true si es urgente
    val correctionOf: String? = null, // id de la OT original si es de corrección

    val plannerId: String? = null,    // uid del planner responsable
    val errorFlag: Boolean = false,   // si tuvo error
    val errorMessage: String? = null, // detalle del error

    val designFileUrl: String? = null, // opcional, enlace al archivo de diseño

    // 🔹 NUEVOS CAMPOS PARA PLANIFICACIÓN / ASIGNACIÓN
    val assignedOperator: String? = null,   // nombre del operario asignado
    val assignedMachine: String? = null,    // máquina / recurso
    val plannedStartTime: String? = null,   // "HH:mm" o "YYYY-MM-DD HH:mm"
    val plannedEndTime: String? = null      // "HH:mm" o "YYYY-MM-DD HH:mm"
)
