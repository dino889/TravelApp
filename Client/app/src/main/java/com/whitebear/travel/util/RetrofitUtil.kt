package com.whitebear.travel.util

import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.src.network.api.*

class RetrofitUtil {
    companion object {
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
        val areaService = ApplicationClass.retrofit.create(AreaApi::class.java)
        val placeService = ApplicationClass.retrofit.create(PlaceApi::class.java)
        val weatherService = ApplicationClass.retrofit.create(WeatherApi::class.java)
        val routeService = ApplicationClass.retrofit.create(RouteApi::class.java)
    }
}