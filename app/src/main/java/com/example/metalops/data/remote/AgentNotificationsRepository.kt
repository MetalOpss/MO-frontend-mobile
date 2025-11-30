package com.example.metalops.data.remote

import com.example.metalops.data.model.AgentNotification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AgentNotificationsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val collection = db.collection("agent_notifications")

    // Si luego quieres filtrar por agente, pásale el agentId y pon un whereEqualTo
    suspend fun getNotifications(): List<AgentNotification> {
        val snapshot = collection
            .orderBy("createdAt") // si tienes el campo como string; ideal sería Timestamp
            .get()
            .await()

        return snapshot.documents.map { doc ->
            AgentNotification(
                id = doc.id,
                title = doc.getString("title") ?: "",
                body = doc.getString("body") ?: "",
                isRead = doc.getBoolean("isRead") ?: false,
                createdAt = doc.getString("createdAt") ?: "",
                agentId = doc.getString("agentId") ?: ""
            )
        }
    }

    suspend fun setNotificationRead(
        notificationId: String,
        isRead: Boolean
    ): Result<Unit> = try {
        collection.document(notificationId)
            .update("isRead", isRead)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
