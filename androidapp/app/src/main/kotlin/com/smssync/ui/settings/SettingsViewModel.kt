package com.smssync.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smssync.util.PreferencesManager
import com.smssync.network.SmsApiService
import com.smssync.network.SmsPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val _serverUrl = MutableStateFlow("")
    val serverUrl = _serverUrl.asStateFlow()

    private val _authToken = MutableStateFlow("")
    val authToken = _authToken.asStateFlow()

    private val _testStatus = MutableStateFlow<String?>(null)
    val testStatus = _testStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                _serverUrl.value = PreferencesManager.getServerUrlSync(context)
                _authToken.value = PreferencesManager.getAuthTokenSync(context)
            } catch (e: Exception) {
                _serverUrl.value = ""
                _authToken.value = ""
            }
        }
    }

    fun updateServerUrl(url: String) {
        _serverUrl.value = url
    }

    fun updateAuthToken(token: String) {
        _authToken.value = token
    }

    fun saveSettings() {
        viewModelScope.launch {
            PreferencesManager.saveServerUrl(context, _serverUrl.value)
            PreferencesManager.saveAuthToken(context, _authToken.value)
            _testStatus.value = "Settings saved"
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val url = _serverUrl.value
                if (url.isEmpty()) {
                    _testStatus.value = "Server URL is empty"
                    _isLoading.value = false
                    return@launch
                }

                val baseUrl = if (url.endsWith("/")) url else "$url/"
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(SmsApiService::class.java)

                val testPayload = SmsPayload(
                    sender = "TEST",
                    timestamp = System.currentTimeMillis(),
                    message = "Test message",
                    bankSms = false
                )

                try {
                    apiService.sendSms("Bearer ${_authToken.value}", testPayload)
                    _testStatus.value = "Connection successful"
                } catch (e: Exception) {
                    _testStatus.value = "Connection failed: ${e.message}"
                }
            } catch (e: Exception) {
                _testStatus.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
