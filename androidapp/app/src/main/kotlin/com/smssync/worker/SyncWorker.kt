package com.smssync.worker

import android.content.Context
import android.provider.Telephony
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smssync.data.db.SmsDatabase
import com.smssync.data.db.SmsEntity
import com.smssync.data.repository.SmsRepository
import com.smssync.network.SmsApiService
import com.smssync.util.BankSmsFilter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val db = SmsDatabase.getDatabase(applicationContext)
            val repository = createRepository(applicationContext, db)

            val cursor = applicationContext.contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                null,
                null,
                null,
                "${Telephony.Sms.DATE} DESC"
            )

            var importedCount = 0
            var syncedCount = 0

            cursor?.use {
                while (it.moveToNext()) {
                    val sender = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    val timestamp = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))

                    if (!repository.checkIfSmsExists(sender, timestamp)) {
                        val isBankSms = BankSmsFilter.isBankSms(sender, body)
                        val sms = SmsEntity(
                            sender = sender,
                            body = body,
                            timestamp = timestamp,
                            isBankSms = isBankSms
                        )
                        repository.insertSms(sms)
                        importedCount++
                    }
                }
            }

            val unsyncedBankSms = repository.getUnsyncedBankSms()
            for (sms in unsyncedBankSms) {
                if (repository.syncSms(sms)) {
                    repository.markAsSynced(sms.id)
                    syncedCount++
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
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
