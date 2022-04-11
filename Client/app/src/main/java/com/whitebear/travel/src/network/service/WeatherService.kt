package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Weather
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response
import retrofit2.Retrofit

class WeatherService {
    suspend fun getWeather(dataType:String,numOfRows:Int, pageNo:Int, baseDate:Int, baseTime:Int, nx:String, ny:String ): Response<Weather>
    = RetrofitUtil.weatherService.getWeather(dataType,numOfRows,pageNo,baseDate,baseTime,nx,ny)
}