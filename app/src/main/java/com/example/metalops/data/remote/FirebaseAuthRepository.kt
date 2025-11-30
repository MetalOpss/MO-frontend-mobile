package com.example.metalops.data.remote

import android.content.Context
import com.example.metalops.core.session.SessionManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.EmailAuthProvider


class FirebaseAuthRepository(context: Context) {

    private val auth = Firebase.auth
    private val usersCollection = Firebase.firestore.collection("users")
    private val sessionManager = SessionManager(context)

    /**
     * Login:
     * 1. Firebase Auth (email + password)
     * 2. Lee users/{uid} en Firestore
     * 3. Guarda name + role en SessionManager
     * 4. Devuelve el rol para navegación
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try {
            // 1. Login en Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("No se pudo obtener el usuario autenticado"))
            val uid = user.uid

            // 2. Leer datos en Firestore: users/{uid}
            val snapshot = usersCollection.document(uid).get().await()
            if (!snapshot.exists()) {
                return Result.failure(Exception("No se encontraron datos del usuario en Firestore"))
            }

            val name = snapshot.getString("name") ?: email
            val role = snapshot.getString("role") ?: "agente"
            val emailFromFirestore = snapshot.getString("email") ?: user.email ?: email

            // 3. Guardar en SessionManager
            sessionManager.saveUserName(name)
            sessionManager.saveUserRole(role)
            sessionManager.saveUserEmail(emailFromFirestore)

            // 4. Devolver rol para navegación
            Result.success(role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
// (asegúrate de tener este import arriba del archivo)

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No hay usuario autenticado"))

            val email = user.email
                ?: return Result.failure(Exception("El usuario no tiene correo asociado"))

            // 1. Re-autenticar con la contraseña actual
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            // 2. Actualizar contraseña
            user.updatePassword(newPassword).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        auth.signOut()
        sessionManager.clearSession()
    }
}
