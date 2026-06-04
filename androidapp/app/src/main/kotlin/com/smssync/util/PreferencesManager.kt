package com.smssync.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object PreferencesManager {
    private val SERVER_URL_KEY = stringPreferencesKey("server_url")
    private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

    fun getServerUrl(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[SERVER_URL_KEY] ?: ""
        }
    }

    fun getAuthToken(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveServerUrl(context: Context, url: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = url
        }
    }

    suspend fun saveAuthToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun getServerUrlSync(context: Context): String {
        return context.dataStore.data.map { preferences ->
            preferences[SERVER_URL_KEY] ?: ""
        }.getFirstValue()
    }

    suspend fun getAuthTokenSync(context: Context): String {
        return context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY] ?: ""
        }.getFirstValue()
    }

    private suspend fun <T> Flow<T>.getFirstValue(): T {
        var value: T? = null
        this.collect { value = it }
        return value ?: throw Exception("No value found")
    }
}
