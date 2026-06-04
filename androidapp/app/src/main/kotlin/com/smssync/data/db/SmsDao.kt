package com.smssync.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {
    @Insert
    suspend fun insert(sms: SmsEntity): Long

    @Update
    suspend fun update(sms: SmsEntity)

    @Query("SELECT * FROM sms ORDER BY timestamp DESC")
    fun getAllSms(): Flow<List<SmsEntity>>

    @Query("SELECT * FROM sms WHERE id = :id")
    suspend fun getSmsById(id: Int): SmsEntity?

    @Query("SELECT * FROM sms WHERE isBankSms = 1 AND synced = 0 ORDER BY timestamp DESC")
    suspend fun getUnsyncedBankSms(): List<SmsEntity>

    @Query("SELECT COUNT(*) FROM sms WHERE sender = :sender AND timestamp = :timestamp")
    suspend fun checkIfExists(sender: String, timestamp: Long): Int

    @Query("SELECT * FROM sms WHERE synced = 0 ORDER BY timestamp DESC")
    suspend fun getUnsyncedSms(): List<SmsEntity>
}
