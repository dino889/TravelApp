package com.whitebear.travel.config

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.KakaoSdk
import com.whitebear.travel.R
import com.whitebear.travel.config.intercepter.AddCookiesInterceptor
import com.whitebear.travel.config.intercepter.ReceivedCookiesInterceptor
import com.whitebear.travel.config.intercepter.XAccessTokenInterceptor
import com.whitebear.travel.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ApplicationClass"
class ApplicationClass : Application() {
    companion object{
        const val SERVER_URL = "http://115.85.180.240:7878/"   // local 서버 실행 시
        //AWS servoer

        const val IMGS_URL = "${SERVER_URL}imgs/"
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit

        // JWT Token Header 키 값
        const val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"

    }

    override fun onCreate() {
        super.onCreate()
        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AddCookiesInterceptor())
//            .addInterceptor(ReceivedCookiesInterceptor())
            .addNetworkInterceptor(XAccessTokenInterceptor()) // JWT 자동 헤더 전송
            .connectTimeout(50, TimeUnit.SECONDS)
            .build()

        // Gson 객체 생성 - setLenient 속성 추가
        val gson : Gson = GsonBuilder()
            .setLenient()
            .create()
        
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()

        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_nativeapp_key))
    }

}