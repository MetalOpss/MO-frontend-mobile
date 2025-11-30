package com.example.metalops.core.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "metalops_session"

// Extension a nivel de Context
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    // -----------------------------
    // LECTURAS (Flow)
    // -----------------------------

    // Nombres nuevos
    val userRoleFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_USER_ROLE] }

    val authTokenFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_AUTH_TOKEN] }

    val userNameFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_USER_NAME] }

    val userEmailFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_USER_EMAIL] }

    // ✅ Alias con los nombres antiguos para no romper MetalOpsApp
    val userRole: Flow<String?> get() = userRoleFlow
    val authToken: Flow<String?> get() = authTokenFlow

    // -----------------------------
    // LECTURAS DIRECTAS (Suspending)
    // -----------------------------

    suspend fun getUserRole(): String? {
        return context.dataStore.data.first()[KEY_USER_ROLE]
    }

    suspend fun getUserName(): String? {
        return context.dataStore.data.first()[KEY_USER_NAME]
    }

    suspend fun getUserEmail(): String? {
        return context.dataStore.data.first()[KEY_USER_EMAIL]
    }

    suspend fun getAuthToken(): String? {
        return context.dataStore.data.first()[KEY_AUTH_TOKEN]
    }

    // -----------------------------
    // ESCRITURAS
    // -----------------------------

    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ROLE] = role
        }
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTH_TOKEN] = token
        }
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
        }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_EMAIL] = email
        }
    }

    // -----------------------------
    // LIMPIAR DATOS
    // -----------------------------
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}