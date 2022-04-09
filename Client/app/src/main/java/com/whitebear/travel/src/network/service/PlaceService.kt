package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response

class PlaceService {
    suspend fun getPlaceByArea(areaName:String) : Response<Message> = RetrofitUtil.placeService.getPlaceByArea(areaName)
    suspend fun getPlaceById(id:Int):Response<Message> = RetrofitUtil.placeService.getPlaceById(id)
}