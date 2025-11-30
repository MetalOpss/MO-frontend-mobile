package com.example.metalops.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class PlannerWorkOrder(
    val id: String,
    val code: String,
    val title: String,
    val client: String,
    val status: String,
    val scheduledDate: String,
    val isUrgent: Boolean,
    val isCorrection: Boolean
)

/**
 * Repositorio que alimenta el dashboard del Planner.
 * Lee directamente de la colección "work-orders" (la misma que usa el Agente).
 */
class PlannerDashboardRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val collection = db.collection("work-orders")

    /**
     * Devuelve todas las OT en un formato resumido para el Planner,
     * ya ordenadas por prioridad:
     *   1. Correcciones primero
     *   2. Luego urgentes
     *   3. Luego el resto, por fecha programada ascendente
     */
    suspend fun getAllWorkOrders(): List<PlannerWorkOrder> {
        val snapshot = collection.get().await()

        val list = snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val title = doc.getString("title") ?: return@mapNotNull null
            val clientName = doc.getString("clientName") ?: "Sin cliente"
            val status = doc.getString("status") ?: "en registro"
            val scheduledDate = doc.getString("scheduledDate") ?: "--"
            val urgent = doc.getBoolean("urgent") ?: false
            val code = doc.getString("code") ?: id   // si no tienes code, usamos el id del doc
            val correctionOf = doc.getString("correctionOf")
            val isCorrection = !correctionOf.isNullOrBlank()

            PlannerWorkOrder(
                id = id,
                code = code,
                title = title,
                client = clientName,
                status = status,
                scheduledDate = scheduledDate,
                isUrgent = urgent,
                isCorrection = isCorrection
            )
        }

        // Corrección > Urgente > resto, y dentro orden por fecha (string YYYY-MM-DD)
        return list.sortedWith(
            compareByDescending<PlannerWorkOrder> { it.isCorrection }
                .thenByDescending { it.isUrgent }
                .thenBy { it.scheduledDate }
        )
    }
}
