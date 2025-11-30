package com.example.metalops.data.remote

import com.example.metalops.data.model.Order
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class OrdersRepository {

    private val ordersRef = Firebase.firestore.collection("orders")

    suspend fun getOrdersByAgent(agentId: String): List<Order> {
        return try {
            val snapshot = ordersRef
                .whereEqualTo("agentId", agentId)
                .get()
                .await()

            snapshot.documents.map { doc ->
                Order(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    status = doc.getString("status") ?: "",
                    date = doc.getString("date") ?: "",
                    agentId = doc.getString("agentId") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
