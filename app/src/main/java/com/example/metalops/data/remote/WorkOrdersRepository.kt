package com.example.metalops.data.remote

import com.example.metalops.data.model.WorkOrder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WorkOrdersRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val collection = db.collection("work-orders")

    // ---------- MAPEO FIRESTORE → WorkOrder ----------

    private fun mapDocToWorkOrder(doc: com.google.firebase.firestore.DocumentSnapshot): WorkOrder? {
        val id = doc.id

        val title = doc.getString("title") ?: return null
        val clientName = doc.getString("clientName") ?: "Sin cliente"
        val location = doc.getString("location") ?: "Sin ubicación"

        val priority = doc.getString("priority") ?: "media"
        val status = doc.getString("status") ?: "en registro"
        val type = doc.getString("type") ?: "normal"

        val scheduledDate = doc.getString("scheduledDate") ?: ""
        val scheduledTime = doc.getString("scheduledTime") ?: ""

        val code = doc.getString("code") ?: id.uppercase().take(8)

        val isUrgent = doc.getBoolean("isUrgent") ?: false
        val correctionOf = doc.getString("correctionOf")

        val plannerId = doc.getString("plannerId")
        val errorFlag = doc.getBoolean("errorFlag") ?: false
        val errorMessage = doc.getString("errorMessage")

        val designFileUrl = doc.getString("designFileUrl")

        // 🔹 Campos de planificación / asignación
        val assignedOperator = doc.getString("assignedOperator")
        val assignedMachine = doc.getString("assignedMachine")
        val plannedStartTime = doc.getString("plannedStartTime")
        val plannedEndTime = doc.getString("plannedEndTime")

        return WorkOrder(
            id = id,
            code = code,
            title = title,
            clientName = clientName,
            location = location,
            priority = priority,
            status = status,
            type = type,
            scheduledDate = scheduledDate,
            scheduledTime = scheduledTime,
            isUrgent = isUrgent,
            correctionOf = correctionOf,
            plannerId = plannerId,
            errorFlag = errorFlag,
            errorMessage = errorMessage,
            designFileUrl = designFileUrl,
            assignedOperator = assignedOperator,
            assignedMachine = assignedMachine,
            plannedStartTime = plannedStartTime,
            plannedEndTime = plannedEndTime
        )
    }

    // ---------- LECTURA ----------

    suspend fun getWorkOrders(): List<WorkOrder> {
        val snapshot = collection.get().await()
        return snapshot.documents.mapNotNull { mapDocToWorkOrder(it) }
    }

    suspend fun getWorkOrdersForPlanner(plannerId: String?): List<WorkOrder> {
        val query = if (plannerId.isNullOrBlank()) {
            collection
        } else {
            collection.whereEqualTo("plannerId", plannerId)
        }

        val snapshot = query.get().await()
        return snapshot.documents.mapNotNull { mapDocToWorkOrder(it) }
    }

    // ---------- CREACIÓN (usa Agente al registrar OT) ----------

    suspend fun addWorkOrder(
        title: String,
        clientName: String,
        location: String,
        priority: String,
        status: String,
        scheduledDate: String,
        scheduledTime: String,
        type: String = "normal",        // normal / urgente / correccion
        isUrgent: Boolean = false,
        correctionOf: String? = null,
        plannerId: String? = null,
        designFileUrl: String? = null
    ): Result<Unit> {
        return try {
            val data = hashMapOf<String, Any?>(
                "title" to title,
                "clientName" to clientName,
                "location" to location,
                "priority" to priority,
                "status" to status,
                "scheduledDate" to scheduledDate,
                "scheduledTime" to scheduledTime,
                "type" to type,
                "isUrgent" to isUrgent,
                "correctionOf" to correctionOf,
                "plannerId" to plannerId,
                "designFileUrl" to designFileUrl,
                // campos de planificación se inicializan vacíos
                "assignedOperator" to null,
                "assignedMachine" to null,
                "plannedStartTime" to null,
                "plannedEndTime" to null
            )

            val docRef = collection.document()
            data["code"] = "OT-${docRef.id.takeLast(4).uppercase()}"

            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ---------- ACTUALIZACIÓN COMPLETA (usa Planner y Agente) ----------

    suspend fun updateWorkOrder(order: WorkOrder): Result<Unit> {
        return try {
            if (order.id.isBlank()) {
                return Result.failure(IllegalArgumentException("El id de la OT está vacío"))
            }

            val data = hashMapOf<String, Any?>(
                "code" to order.code,
                "title" to order.title,
                "clientName" to order.clientName,
                "location" to order.location,
                "priority" to order.priority,
                "status" to order.status,
                "type" to order.type,
                "scheduledDate" to order.scheduledDate,
                "scheduledTime" to order.scheduledTime,
                "isUrgent" to order.isUrgent,
                "correctionOf" to order.correctionOf,
                "plannerId" to order.plannerId,
                "errorFlag" to order.errorFlag,
                "errorMessage" to order.errorMessage,
                "designFileUrl" to order.designFileUrl,
                // 🔹 planificación / asignación
                "assignedOperator" to order.assignedOperator,
                "assignedMachine" to order.assignedMachine,
                "plannedStartTime" to order.plannedStartTime,
                "plannedEndTime" to order.plannedEndTime
            )

            collection.document(order.id).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ---------- ACTUALIZACIÓN RÁPIDA DESDE PLANNER ----------

    suspend fun updatePlannerFields(
        id: String,
        status: String,
        priority: String,
        type: String,
        scheduledDate: String,
        scheduledTime: String,
        isUrgent: Boolean,
        correctionOf: String?
    ): Result<Unit> {
        if (id.isBlank()) return Result.failure(IllegalArgumentException("id vacío"))

        return try {
            val data = mapOf(
                "status" to status,
                "priority" to priority,
                "type" to type,
                "scheduledDate" to scheduledDate,
                "scheduledTime" to scheduledTime,
                "isUrgent" to isUrgent,
                "correctionOf" to correctionOf
            )
            collection.document(id).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // ---------- ELIMINAR ----------

    suspend fun deleteWorkOrder(id: String): Result<Unit> {
        return try {
            if (id.isBlank()) {
                return Result.failure(IllegalArgumentException("El id de la OT está vacío"))
            }
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}