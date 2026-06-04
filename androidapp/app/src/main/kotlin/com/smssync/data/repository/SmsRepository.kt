package com.smssync.data.repository

import android.content.Context
import com.smssync.data.db.SmsDao
import com.smssync.data.db.SmsEntity
import com.smssync.network.SmsApiService
import com.smssync.network.SmsPayload
import com.smssync.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class SmsRepository(
    private val smsDao: SmsDao,
    private val apiService: SmsApiService,
    private val context: Context
) {
    fun getAllSms(): Flow<List<SmsEntity>> = smsDao.getAllSms()

    suspend fun insertSms(sms: SmsEntity): Long = smsDao.insert(sms)

    suspend fun updateSms(sms: SmsEntity) = smsDao.update(sms)

    suspend fun checkIfSmsExists(sender: String, timestamp: Long): Boolean {
        return smsDao.checkIfExists(sender, timestamp) > 0
    }

    suspend fun syncSms(sms: SmsEntity): Boolean {
        return try {
            val token = PreferencesManager.getAuthTokenSync(context)
            val url = PreferencesManager.getServerUrlSync(context)

            if (token.isEmpty() || url.isEmpty()) {
                return false
            }

            val payload = SmsPayload(
                sender = sms.sender,
                timestamp = sms.timestamp,
                message = sms.body,
                bankSms = sms.isBankSms
            )

            val response = apiService.sendSms("Bearer $token", payload)
            true
        } catch (e: HttpException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun markAsSynced(smsId: Int) {
        val sms = smsDao.getSmsById(smsId)
        if (sms != null) {
            smsDao.update(
                sms.copy(
                    synced = true,
                    syncedAt = System.currentTimeMillis(),
                    errorMessage = null
                )
            )
        }
    }

    suspend fun markAsSyncFailed(smsId: Int, errorMessage: String) {
        val sms = smsDao.getSmsById(smsId)
        if (sms != null) {
            smsDao.update(
                sms.copy(
                    synced = false,
                    errorMessage = errorMessage
                )
            )
        }
    }

    suspend fun getUnsyncedBankSms(): List<SmsEntity> = smsDao.getUnsyncedBankSms()

    suspend fun getUnsyncedSms(): List<SmsEntity> = smsDao.getUnsyncedSms()
}
