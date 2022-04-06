package com.whitebear.travel.src.network.api

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.User
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    /**
     * 유저정보 조회
     * */
    @GET("/users/15")
    suspend fun getUsers() : Response<Message>
    /**
     * 유저정보 입력 = 회원가입
     * */
    @POST("/users")
    suspend fun insertUser(@Body urlencode: User) : Response<Message>
    /**
     * 유저정보 수정
     * */
    @PUT("/users/3")
    suspend fun updateUser(@Body urlencode :User) : Response<Message>
    /**
     * 로그인
     * */
    @POST("/users/login")
    suspend fun login(@Body urlencode: User) : Response<Message>
    /**
     * 유저 탈퇴
     * */
    @DELETE("/users/3")
    suspend fun deleteUser() : Response<Message>
    /**
     * 이메일 중복체크
     * */
    @GET("users/exist/")
    suspend fun doubleCheckEmail(@Path ("email") email:String) : Response<Message>
}