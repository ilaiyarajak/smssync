package com.smssync.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smssync.data.db.SmsDatabase
import com.smssync.data.db.SmsEntity
import com.smssync.data.repository.SmsRepository
import com.smssync.network.SmsApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.provider.Telephony

data class SyncStats(
    val totalScanned: Int = 0,
    val imported: Int = 0,
    val synced: Int = 0,
    val failed: Int = 0
)

class LogsViewModel(private val context: Context) : ViewModel() {
    private val db = SmsDatabase.getDatabase(context)
    private val smsDao = db.smsDao()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(SmsApiService::class.java)
    private val repository = SmsRepository(smsDao, apiService, context)

    val smsList = repository.getAllSms()

    private val _syncStats = MutableStateFlow<SyncStats?>(null)
    val syncStats = _syncStats.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    fun syncSms(sms: SmsEntity) {
        viewModelScope.launch {
            try {
                if (repository.syncSms(sms)) {
                    repository.markAsSynced(sms.id)
                } else {
                    repository.markAsSyncFailed(sms.id, "Sync failed")
                }
            } catch (e: Exception) {
                repository.markAsSyncFailed(sms.id, e.message ?: "Unknown error")
            }
        }
    }

    fun masterSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val cursor = context.contentResolver.query(
                    Telephony.Sms.CONTENT_URI,
                    null,
                    null,
                    null,
                    "${Telephony.Sms.DATE} DESC"
                )

                var totalScanned = 0
                var imported = 0

                cursor?.use {
                    while (it.moveToNext()) {
                        totalScanned++
                        val sender = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                        val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                        val timestamp = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))

                        if (!repository.checkIfSmsExists(sender, timestamp)) {
                            val isBankSms = com.smssync.util.BankSmsFilter.isBankSms(sender, body)
                            val sms = SmsEntity(
                                sender = sender,
                                body = body,
                                timestamp = timestamp,
                                isBankSms = isBankSms
                            )
                            repository.insertSms(sms)
                            imported++
                        }
                    }
                }

                val unsyncedBankSms = repository.getUnsyncedBankSms()
                var synced = 0
                var failed = 0

                for (sms in unsyncedBankSms) {
                    if (repository.syncSms(sms)) {
                        repository.markAsSynced(sms.id)
                        synced++
                    } else {
                        failed++
                    }
                }

                _syncStats.value = SyncStats(
                    totalScanned = totalScanned,
                    imported = imported,
                    synced = synced,
                    failed = failed
                )
            } catch (e: Exception) {
                _syncStats.value = SyncStats()
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
