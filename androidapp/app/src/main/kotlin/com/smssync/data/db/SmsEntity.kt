package com.smssync.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms")
data class SmsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sender: String,
    val body: String,
    val timestamp: Long,
    val isBankSms: Boolean,
    val synced: Boolean = false,
    val syncedAt: Long? = null,
    val errorMessage: String? = null
)
