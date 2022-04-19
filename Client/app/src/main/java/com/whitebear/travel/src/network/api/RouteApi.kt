package com.whitebear.travel.src.network.api

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.Message2
import com.whitebear.travel.src.dto.RouteLike
import com.whitebear.travel.src.dto.RouteReview
import retrofit2.Response
import retrofit2.http.*

interface RouteApi {
    @GET("place_list")
    suspend fun getRouteByArea(@Query("areaName") areaName:String) : Response<Message>

    @GET("place_list")
    suspend fun getRouteByAreaToSort(@Query("areaName") areaName:String,@Query("sort") sorting:String) : Response<Message>

    @GET("place_list/userlike/{id}")
    suspend fun getRouteLikeByUser(@Path("id")userId:Int) : Response<Message2>

    @POST("place_list/review")
    suspend fun insertRouteReview(@Body review: RouteReview) : Response<Message>

    @GET("place_list/review")
    suspend fun getRouteReviewDetail(@Query("placeListId") routeId:Int) : Response<Message>

    @DELETE("place_list/review")
    suspend fun deleteRouteReview(@Path("reviewId")reviewId:Int) : Response<Message>

    @POST("place_list/like")
    suspend fun routeLike(@Body routeLike: RouteLike) : Response<Message>
}