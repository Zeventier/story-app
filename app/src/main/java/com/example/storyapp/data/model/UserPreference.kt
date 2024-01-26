package com.example.storyapp.data.model

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.view.login.LoginViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                preferences[USERID_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun login(user: LoginResult): Boolean {
        dataStore.edit { preferences ->
            preferences[USERID_KEY] = user.userId
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
        }
        return true
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[USERID_KEY] = ""
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USERID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}