package com.smssync.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.smssync.data.db.SmsDatabase
import com.smssync.data.db.SmsEntity
import com.smssync.data.repository.SmsRepository
import com.smssync.network.SmsApiService
import com.smssync.util.BankSmsFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.smssync.util.PreferencesManager

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (message in messages) {
                val sender = message.originatingAddress ?: "Unknown"
                val body = message.messageBody
                val timestamp = message.timestampMillis

                val isBankSms = BankSmsFilter.isBankSms(sender, body)

                val sms = SmsEntity(
                    sender = sender,
                    body = body,
                    timestamp = timestamp,
                    isBankSms = isBankSms
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val db = SmsDatabase.getDatabase(context)
                    val repository = createRepository(context, db)

                    val exists = repository.checkIfSmsExists(sender, timestamp)
                    if (!exists) {
                        val id = repository.insertSms(sms)

                        if (isBankSms) {
                            try {
                                if (repository.syncSms(sms)) {
                                    repository.markAsSynced(id.toInt())
                                } else {
                                    repository.markAsSyncFailed(id.toInt(), "Sync failed")
                                }
                            } catch (e: Exception) {
                                repository.markAsSyncFailed(id.toInt(), e.message ?: "Unknown error")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createRepository(context: Context, db: SmsDatabase): SmsRepository {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(SmsApiService::class.java)
        return SmsRepository(db.smsDao(), apiService, context)
    }
}
