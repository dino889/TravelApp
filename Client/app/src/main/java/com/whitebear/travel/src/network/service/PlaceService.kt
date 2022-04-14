package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.Message2
import com.whitebear.travel.src.dto.PlaceLike
import com.whitebear.travel.src.dto.PlaceReview
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response

class PlaceService {
    suspend fun getPlaceByArea(areaName:String) : Response<Message> = RetrofitUtil.placeService.getPlaceByArea(areaName)
    suspend fun getPlaceById(id:Int):Response<Message> = RetrofitUtil.placeService.getPlaceById(id)

    suspend fun insertPlaceReview(placeReview:PlaceReview) : Response<Message> = RetrofitUtil.placeService.insertPlaceReview(placeReview)

    suspend fun getPlaceReview(placeId:Int) : Response<Message> = RetrofitUtil.placeService.getPlaceReview(placeId)

    suspend fun deletePlaceReview(reviewId:Int) : Response<Message> = RetrofitUtil.placeService.deletePlaceReview(reviewId)

    suspend fun getLikePlaceByUser(userId:Int) : Response<Message2> = RetrofitUtil.placeService.getLikePlacebyUser(userId)

    suspend fun placeLike(placeLike: PlaceLike) : Response<Message> = RetrofitUtil.placeService.placeLike(placeLike)
}