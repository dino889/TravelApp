package com.whitebear.travel.src.network.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationApi {

    /**
     * userID로 받은 알림들 가져오기
     * */
    @GET("notification/{userId}")
    suspend fun selectNotificationByUser(@Path("userId") userId: Int) : Response<HashMap<String, Any>>

    /**
     * 알림 지우기 by ID
     */
    @DELETE("notification/{id}}")
    suspend fun deleteNotification(@Path("id") id: Int) : Response<HashMap<String, Any>>
}