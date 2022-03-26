package tech.kotlinx.knox.data.repository

import kotlinx.coroutines.flow.Flow

class RepositoryImpl(private val dataStore: UserPreferences): Repository {
    override suspend fun getUserName(): Flow<String?> {
        return dataStore.getUserName()
    }

    override suspend fun saveUserName(userName: String) {
        return dataStore.saveUserName(userName)
    }

    override suspend fun deleteUserPreferences() {
        return dataStore.deleteUserPreferences()
    }

}