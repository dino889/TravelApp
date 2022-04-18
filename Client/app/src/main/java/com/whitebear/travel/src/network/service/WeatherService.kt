package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Weather
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response

class WeatherService {
    suspend fun getWeather(dataType:String,numOfRows:Int, pageNo:Int, baseDate:Int, baseTime:String, nx:String, ny:String ): Response<Weather>
    = RetrofitUtil.weatherService.getWeather(dataType,numOfRows,pageNo,baseDate,baseTime,nx,ny)

    suspend fun getAirQuality(stationName:String) = RetrofitUtil.weatherService.getAirQuality(stationName)

    suspend fun getMeasure(tmX:Double, tmY:Double) = RetrofitUtil.weatherService.getMeasure(tmX,tmY)

    suspend fun getTmCoordinates(x:Double, y:Double) = RetrofitUtil.weatherService.getTmCoordinates(x,y)
}