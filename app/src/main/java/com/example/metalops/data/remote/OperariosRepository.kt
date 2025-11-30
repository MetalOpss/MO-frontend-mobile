package com.example.metalops.data.remote

import com.example.metalops.data.model.Operario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para leer/escribir operarios en Firestore.
 *
 * Colección usada: "operarios"
 */
class OperariosRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val collection = db.collection("operarios")

    /**
     * Obtiene todos los operarios registrados.
     */
    suspend fun getOperarios(): List<Operario> {
        val snapshot = collection.get().await()

        return snapshot.documents.map { doc ->
            val nombre = doc.getString("nombre") ?: doc.getString("name") ?: ""
            val dni = doc.getString("dni") ?: ""
            val estado = doc.getString("estado") ?: "disponible"

            Operario(
                id = doc.id,
                nombre = nombre,
                dni = dni,
                estado = estado
            )
        }
    }

    /**
     * Crea un nuevo operario en Firestore.
     *
     * @param nombre Nombre completo del operario
     * @param dni    DNI o documento
     * @param estado Estado inicial (por defecto "disponible")
     */
    suspend fun createOperario(
        nombre: String,
        dni: String,
        estado: String = "disponible"
    ): Operario {
        val data = hashMapOf(
            "nombre" to nombre,
            "dni" to dni,
            "estado" to estado
        )

        val ref = collection.add(data).await()

        return Operario(
            id = ref.id,
            nombre = nombre,
            dni = dni,
            estado = estado
        )
    }

    /**
     * Actualiza el estado de un operario (disponible, ocupado, en_pausa).
     */
    suspend fun updateEstado(
        operarioId: String,
        nuevoEstado: String
    ) {
        collection.document(operarioId)
            .update("estado", nuevoEstado)
            .await()
    }
}