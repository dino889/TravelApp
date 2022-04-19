package com.whitebear.travel.src.network.api

import com.whitebear.travel.src.dto.*
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

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
    suspend fun deletePlaceReview(@Path("id")reviewId:Int) : Response<Message>

    @POST("/places/like")
    suspend fun placeLike(@Body placeLike: PlaceLike) : Response<Message>

    @GET("places/userlike/{id}")
    suspend fun getLikePlacebyUser(@Path("id") userId:Int) : Response<Message2>

    @GET("places")
    suspend fun getPlaceByAreaToSort(@Query("areaName") areaName : String, @Query("sort")sort:String) : Response<Message>

}