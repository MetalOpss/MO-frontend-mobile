package com.example.metalops.core.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "metalops_session"

// Extension única a nivel de Context
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    // Lecturas (Flow)
    val userRole: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_USER_ROLE] }

    val authToken: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_AUTH_TOKEN] }

    // Escrituras (suspend)
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

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
