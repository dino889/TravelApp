package com.whitebear.travel.src.network.api

import com.whitebear.travel.src.dto.airQuality.AirQuality
import com.whitebear.travel.src.dto.Weather
import com.whitebear.travel.src.dto.stationResponse.StationResponse
import com.whitebear.travel.src.dto.tm.TmCoordinatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
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


    @GET("http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey=02DFWIMCh4ja1%2FJLxBL94may6yu73Byr8JXuqG6WvW9e4DTCgPCJZGHksn4qJ%2F1yKl9Vv7TWf5nxyjOPiArNuw%3D%3D&returnType=json")
    suspend fun getMeasure(@Query("tmX")tmX:Double,
                           @Query("tmY")tmY:Double ) : Response<StationResponse>

    @GET("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=02DFWIMCh4ja1%2FJLxBL94may6yu73Byr8JXuqG6WvW9e4DTCgPCJZGHksn4qJ%2F1yKl9Vv7TWf5nxyjOPiArNuw%3D%3D&returnType=json&dataTerm=DAILY&ver=1.3")
    suspend fun getAirQuality(@Query("stationName") stationName : String) : Response<AirQuality>

    @Headers("Authorization: KakaoAK fb5906ccb1827dfeca20dad4abdcc1c8")
    @GET("https://dapi.kakao.com/v2/local/geo/transcoord.json?output_coord=TM")
    suspend fun getTmCoordinates(@Query("x")long:Double, @Query("y")lat:Double):Response<TmCoordinatesResponse>
}