package com.example.metalops.core.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instance ligada al contexto
private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    // Clave donde vamos a guardar el rol
    private val KEY_ROLE = stringPreferencesKey("user_role")

    // Guardar rol después de login (Planner / Agente / Operario)
    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ROLE] = role
        }
    }

    // Leer el rol guardado (null si no hay sesión)
    val userRoleFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ROLE]
    }

    // Logout: borrar el rol
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ROLE)
        }
    }
}
