package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response

class AreaService {
    suspend fun getArea() : Response<Message> = RetrofitUtil.areaService.getArea()
}