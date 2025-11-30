package com.example.metalops.data.remote

import com.example.metalops.data.model.Client
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClientesRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val clientsRef = db.collection("clients")   // 👉 colección "clients"

    suspend fun getClients(): List<Client> {
        return try {
            val uid = auth.currentUser?.uid

            val query = if (uid != null) {
                clientsRef.whereEqualTo("agentId", uid)
            } else {
                clientsRef
            }

            val snapshot = query.get().await()

            snapshot.documents.map { doc ->
                Client(
                    id = doc.id,
                    name = (doc.getString("name") ?: "").trim('"'),
                    company = (doc.getString("company") ?: "").trim('"'),
                    email = (doc.getString("email") ?: "").trim('"'),
                    phone = (doc.getString("phone") ?: "").trim('"'),
                    document = (doc.getString("document") ?: "").trim('"'),
                    agentId = (doc.getString("agentId") ?: "").trim('"')
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addClient(
        name: String,
        company: String,
        email: String,
        phone: String,
        document: String
    ): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(IllegalStateException("Usuario no autenticado"))

            val data = hashMapOf(
                "name" to name,
                "company" to company,
                "email" to email,
                "phone" to phone,
                "document" to document,
                "agentId" to uid
            )

            clientsRef.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
