package com.whitebear.travel.src.network.api

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.dto.PlaceReview
import retrofit2.Response
import retrofit2.http.*

interface PlaceApi {

    @GET("places")
    suspend fun getPlaceByArea(@Query("areaName") areaName : String) : Response<Message>

    @GET("places/{id}")
    suspend fun getPlaceById(@Path ("id")id:Int) : Response<Message>

    @POST("places/review")
    suspend fun insertPlaceReview(@Body placeReview:PlaceReview) : Response<Message>

    @GET("places/review")
    suspend fun getPlaceReview(@Query ("placeId") placeId:Int) : Response<Message>

    @DELETE("places/review/{id}")
    suspend fun deletePlaceReview(@Query("id")reviewId:Int) : Response<Message>

//    @POST("/places/like")
//    suspend fun placeLike(@Body placeLike: Place) : Response<Message>

}