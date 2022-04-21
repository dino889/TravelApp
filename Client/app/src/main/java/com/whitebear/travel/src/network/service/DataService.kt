package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Weather
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response
import kotlin.math.ln

class DataService {
    suspend fun getWeather(dataType:String,numOfRows:Int, pageNo:Int, baseDate:Int, baseTime:String, nx:String, ny:String ): Response<Weather>
    = RetrofitUtil.dataService.getWeather(dataType,numOfRows,pageNo,baseDate,baseTime,nx,ny)

    suspend fun getAirQuality(stationName:String) = RetrofitUtil.dataService.getAirQuality(stationName)

    suspend fun getMeasure(tmX:Double, tmY:Double) = RetrofitUtil.dataService.getMeasure(tmX,tmY)

    suspend fun getTmCoordinates(x:Double, y:Double) = RetrofitUtil.dataService.getTmCoordinates(x,y)

    suspend fun getCovidState(startCreateDt: String, endCreateDt: String) = RetrofitUtil.dataService.getCovidState(startCreateDt, endCreateDt)

    suspend fun getCamping(lat: Double, lng: Double, radius: Int) = RetrofitUtil.dataService.getCamping(lat, lng, radius, "json")
}