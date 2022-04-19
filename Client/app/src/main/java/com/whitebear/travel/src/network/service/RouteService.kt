package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.RouteLike
import com.whitebear.travel.src.dto.RouteReview
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response
import retrofit2.Retrofit

class RouteService {

    suspend fun getRouteByArea(areaName:String) : Response<Message> = RetrofitUtil.routeService.getRouteByArea(areaName)

    suspend fun getRouteByAreaToSort(areaName:String, sort:String) : Response<Message> = RetrofitUtil.routeService.getRouteByAreaToSort(areaName,sort)

    suspend fun getRouteLikeByUser(userId:Int) : Response<Message> = RetrofitUtil.routeService.getRouteLikeByUser(userId)

    suspend fun insertRouteReview(review:RouteReview) : Response<Message> = RetrofitUtil.routeService.insertRouteReview(review)

    suspend fun getRouteReviewDetail(routeId:Int) : Response<Message> = RetrofitUtil.routeService.getRouteReviewDetail(routeId)

    suspend fun deleteRouteReview(reviewId:Int) : Response<Message> = RetrofitUtil.routeService.deleteRouteReview(reviewId)

    suspend fun routeLike(routeLike: RouteLike) : Response<Message> = RetrofitUtil.routeService.routeLike(routeLike)
}