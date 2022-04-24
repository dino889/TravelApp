package com.whitebear.travel.util

import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.src.network.api.AreaApi
import com.whitebear.travel.src.network.api.PlaceApi
import com.whitebear.travel.src.network.api.UserApi
import com.whitebear.travel.src.network.api.DataApi
import com.whitebear.travel.src.network.api.*

class RetrofitUtil {
    companion object {
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
        val areaService = ApplicationClass.retrofit.create(AreaApi::class.java)
        val placeService = ApplicationClass.retrofit.create(PlaceApi::class.java)
        val dataService = ApplicationClass.retrofit.create(DataApi::class.java)
        val routeService = ApplicationClass.retrofit.create(RouteApi::class.java)
        val fcmService = ApplicationClass.retrofit.create(FCMApi::class.java)
        val notificationService = ApplicationClass.retrofit.create(NotificationApi::class.java)
    }
}