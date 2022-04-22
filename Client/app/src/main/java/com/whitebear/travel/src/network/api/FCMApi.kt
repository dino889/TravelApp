package com.whitebear.travel.src.network.api

import retrofit2.Response
import retrofit2.http.*

interface FCMApi {

    // Token 정보 서버로 전송
    @POST("/fcm/token")
    suspend fun uploadToken(@Query("token") token: String, @Query("userId") userId: Int): Response<HashMap<String, Any>>

}