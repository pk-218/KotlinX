package tech.kotlinx.knox.data.repository

import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getUserName(): Flow<String?>
    suspend fun saveUserName(userName: String)
    suspend fun deleteUserPreferences(): Unit
}