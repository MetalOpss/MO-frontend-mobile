package com.example.metalops.data.remote

import com.example.metalops.data.model.WorkOrderStep
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

data class OperarioBasic(
    val id: String,
    val name: String
)

data class PlannerTimelineItem(
    val id: String,
    val workOrderId: String,
    val workOrderCode: String?,
    val serviceType: String,
    val operarioName: String,
    val status: String,
    val timestamp: String   // "YYYY-MM-DD HH:mm"
)

class PlannerStepsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val workOrdersCollection = db.collection("work-orders")

    // ---------- OPERARIOS ----------

    suspend fun getOperarios(): List<OperarioBasic> {
        val snapshot = db.collection("users")
            .whereEqualTo("role", "operario")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id
            val name = doc.getString("name")
                ?: doc.getString("nombre")
                ?: doc.getString("email")
            if (name != null) OperarioBasic(id, name) else null
        }
    }

    // ---------- PASOS / FLUJO DE SERVICIOS ----------

    suspend fun getStepsForOrder(workOrderId: String): List<WorkOrderStep> {
        val snapshot = workOrdersCollection
            .document(workOrderId)
            .collection("steps")
            .orderBy("index")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            WorkOrderStep(
                id = doc.id,
                index = (doc.getLong("index") ?: 0L).toInt(),
                serviceType = doc.getString("serviceType") ?: "",
                operarioId = doc.getString("operarioId") ?: "",
                operarioName = doc.getString("operarioName") ?: "",
                machine = doc.getString("machine") ?: "",
                estimatedStart = doc.getString("estimatedStart") ?: "",
                estimatedEnd = doc.getString("estimatedEnd") ?: "",
                status = doc.getString("status") ?: "bloqueada",
                completedAt = doc.getString("completedAt") ?: ""
            )
        }
    }

    /**
     * Guarda la planificación completa de una OT.
     * - Sobrescribe la subcolección steps.
     * - Marca el primer paso como "liberada", el resto "bloqueada" si aún no tienen estado.
     */
    suspend fun saveStepsForOrder(
        workOrderId: String,
        steps: List<WorkOrderStep>
    ): Result<Unit> {
        return try {
            val orderRef = workOrdersCollection.document(workOrderId)
            val batch = db.batch()

            // Borrar pasos antiguos
            val oldSteps = orderRef.collection("steps").get().await()
            oldSteps.documents.forEach { batch.delete(it.reference) }

            // Insertar nuevos pasos
            steps.sortedBy { it.index }.forEachIndexed { idx, step ->
                val ref = orderRef.collection("steps").document()
                val status = if (step.status.isNotBlank()) {
                    step.status
                } else {
                    if (idx == 0) "liberada" else "bloqueada"
                }

                val data = hashMapOf<String, Any?>(
                    "index" to idx,
                    "serviceType" to step.serviceType,
                    "operarioId" to step.operarioId,
                    "operarioName" to step.operarioName,
                    "machine" to step.machine,
                    "estimatedStart" to step.estimatedStart,
                    "estimatedEnd" to step.estimatedEnd,
                    "status" to status,
                    "completedAt" to step.completedAt
                )

                batch.set(ref, data)
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ---------- LÍNEA DE TIEMPO (timeline-events) ----------

    /**
     * Trae los últimos eventos de la colección "timeline-events"
     * generados cuando el planner guarda la planificación.
     */
    suspend fun getRecentTimelineItems(limit: Int = 30): List<PlannerTimelineItem> {
        val snapshot = db.collection("timeline-events")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        return snapshot.documents.mapNotNull { doc ->
            val ts = doc.getTimestamp("timestamp") ?: return@mapNotNull null

            val workOrderId = doc.getString("workOrderId") ?: ""
            val workOrderCode = doc.getString("workOrderCode") // opcional
            val serviceType =
                doc.getString("workOrderTitle")          // Título de la OT
                    ?: doc.getString("type")             // "planificacion", etc.
                    ?: "Planificación"

            val operarioName = doc.getString("operator") ?: ""
            val status = doc.getString("status") ?: ""

            PlannerTimelineItem(
                id = doc.id,
                workOrderId = workOrderId,
                workOrderCode = workOrderCode,
                serviceType = serviceType,
                operarioName = operarioName,
                status = status,
                timestamp = formatter.format(ts.toDate()) // "YYYY-MM-DD HH:mm"
            )
        }
    }
}
