package com.whitebear.travel.src.network.api

import retrofit2.Response
import retrofit2.http.*

interface FCMApi {

    @FormUrlEncoded
    @POST("notification/{userId}")
    suspend fun sendMsgToUser(@Path("userId") userId: Int, @Field("title") title: String, @Field("body") body: String, @Field("type") type: String) : Response<HashMap<String, Any>>

}