package com.whitebear.travel.src.network.api

import com.whitebear.travel.src.dto.Message
import retrofit2.Response
import retrofit2.http.GET

interface AreaApi {

    @GET("/areas")
    suspend fun getArea():Response<Message>

    @GET("/places/category")
    suspend fun getCategory() : Response<Message>
}