package com.example.metalops.data.remote

import com.example.metalops.core.ui.components.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Las tareas del Agente en el Home son resúmenes de Órdenes de Trabajo.
 *
 * Colección: work-orders
 *
 * Campos que se usan (todos opcionales menos title):
 * - code: String?         (si falta, se usa el id del documento)
 * - title: String         (título / descripción corta)
 * - clientName: String?   (nombre del cliente)
 * - status: String?       (en registro, en progreso, por corregir, en ejecución, completada)
 * - scheduledDate: String?(fecha programada)
 * - urgent: Boolean?      (true si es urgente)
 * - agentId: String?      (UID del agente asignado; si falta, igual se muestra)
 */
class AgenteTasksRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val collectionName = "work-orders"

    suspend fun getTasks(): List<Task> {
        val currentUserId = auth.currentUser?.uid

        // De momento NO filtramos directamente por agentId en la query
        val snapshot = db.collection(collectionName)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            // --- Lectura de campos ---
            val title = doc.getString("title") ?: return@mapNotNull null
            val clientName = doc.getString("clientName") ?: "Sin cliente"
            val statusRaw = doc.getString("status") ?: "en registro"
            val scheduledDate = doc.getString("scheduledDate") ?: "--"
            val urgent = doc.getBoolean("urgent") ?: false

            // Si no hay campo "code", usamos el id del documento como código
            val code = doc.getString("code") ?: doc.id

            // Si el doc tiene agentId y NO es el usuario actual, lo descartamos.
            // (Si el campo no existe, igual se muestra.)
            val agentId = doc.getString("agentId")
            if (currentUserId != null && agentId != null && agentId != currentUserId) {
                return@mapNotNull null
            }

            // Estado para la UI: Pendientes / Urgentes / Completadas
            val uiStatus = when {
                statusRaw.equals("completada", ignoreCase = true) -> "completada"
                urgent -> "urgente"
                else -> "pendiente"
            }

            Task(
                time = scheduledDate,
                name = "$code • $title",
                description = "Cliente: $clientName",
                status = uiStatus
            )
        }
    }
}
