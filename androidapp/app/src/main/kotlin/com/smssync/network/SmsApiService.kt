package com.smssync.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class SmsPayload(
    val sender: String,
    val timestamp: Long,
    val message: String,
    val bankSms: Boolean
)

interface SmsApiService {
    @POST(".")
    suspend fun sendSms(
        @Header("Authorization") authHeader: String,
        @Body payload: SmsPayload
    ): ApiResponse
}

data class ApiResponse(
    val status: String? = null,
    val message: String? = null
)
