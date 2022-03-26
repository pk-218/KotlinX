package tech.kotlinx.knox.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val USER_PREFERENCES_NAME = "user_preferences"

val Context.datastore : DataStore<Preferences> by  preferencesDataStore(name = USER_PREFERENCES_NAME)

class UserPreferences(private val context: Context) {
    companion object {
        val USER_NAME = stringPreferencesKey("NAME")
    }

    fun getUserName(): Flow<String?> = context.datastore.data.map { preferences ->
        preferences[USER_NAME]
    }

    suspend fun saveUserName(userName: String) {
        context.datastore.edit { mutablePreferences ->
            mutablePreferences[USER_NAME] = userName
        }
    }

    suspend fun deleteUserPreferences() {
        context.datastore.edit { mutablePreferences ->
            mutablePreferences.clear()
        }
    }

}