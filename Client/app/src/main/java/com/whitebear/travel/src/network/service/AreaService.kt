package com.whitebear.travel.src.network.service

import com.whitebear.travel.util.RetrofitUtil

class AreaService {

    suspend fun getArea() = RetrofitUtil.areaService.getArea()
}