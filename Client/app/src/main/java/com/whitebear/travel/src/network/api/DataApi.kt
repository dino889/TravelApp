package com.whitebear.travel.src.network.api

import androidx.annotation.XmlRes
import com.tickaroo.tikxml.annotation.Xml
import com.whitebear.travel.src.dto.airQuality.AirQuality
import com.whitebear.travel.src.dto.Weather
import com.whitebear.travel.src.dto.camping.Camping
import com.whitebear.travel.src.dto.covid.Covid
import com.whitebear.travel.src.dto.stationResponse.StationResponse
import com.whitebear.travel.src.dto.tm.TmCoordinatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface DataApi {
    @GET("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=02DFWIMCh4ja1%2FJLxBL94may6yu73Byr8JXuqG6WvW9e4DTCgPCJZGHksn4qJ%2F1yKl9Vv7TWf5nxyjOPiArNuw%3D%3D")
    suspend fun getWeather(@Query("dataType") data_type:String,
                           @Query("numOfRows")num_of_rows:Int,
                           @Query("pageNo")page_no:Int,
                           @Query("base_date") base_date : Int,
                           @Query("base_time") base_time : String,
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


    @Headers("Content-Type: application/xml")
    @GET("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey=mp%2Bwyp26riz6pcVoD5kh8hTaDST8RtkblivAF1iro%2FIGvP950xdhKViJgSnBnGGu0kDp5m%2BKUG5L6xC1nI%2BL1w%3D%3D")
    suspend fun getCovidState(@Query("startCreateDt") startCreateDt: String, @Query("endCreateDt") endCreateDt: String) : Response<Array<Covid>>

    @GET("http://api.visitkorea.or.kr/openapi/service/rest/GoCamping/locationBasedList?serviceKey=mp%2Bwyp26riz6pcVoD5kh8hTaDST8RtkblivAF1iro%2FIGvP950xdhKViJgSnBnGGu0kDp5m%2BKUG5L6xC1nI%2BL1w%3D%3D&MobileOS=AND&MobileApp=TravelApplication&numOfRows=100")
    suspend fun getCamping(@Query("mapX") mapX: Double, @Query("mapY") mapY: Double, @Query("radius") radius: Int, @Query("_type") _type: String) : Response<Camping>
}