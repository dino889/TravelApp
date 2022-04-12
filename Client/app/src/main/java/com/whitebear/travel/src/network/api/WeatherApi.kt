package com.whitebear.travel.src.network.api

import com.whitebear.travel.src.dto.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=02DFWIMCh4ja1%2FJLxBL94may6yu73Byr8JXuqG6WvW9e4DTCgPCJZGHksn4qJ%2F1yKl9Vv7TWf5nxyjOPiArNuw%3D%3D")
    suspend fun getWeather(@Query("dataType") data_type:String,
                           @Query("numOfRows")num_of_rows:Int,
                           @Query("pageNo")page_no:Int,
                           @Query("base_date") base_date : Int,
                           @Query("base_time") base_time : Int,
                           @Query("nx") nx : String,
                           @Query("ny") ny : String) : Response<Weather>




}