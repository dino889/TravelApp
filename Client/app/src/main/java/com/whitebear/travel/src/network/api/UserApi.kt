package com.whitebear.travel.src.network.api

import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.User
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    /**
     * 유저정보 조회
     * */
    @GET("users/{id}")
    suspend fun selectUser(@Path("id") id: Int) : Response<HashMap<String, Any>>

    /**
     * 유저 생성 = 회원가입
     * */
    @FormUrlEncoded
    @POST("users")
//    suspend fun insertUser(@Body urlencoded: User) : Response<HashMap<String, Any>>
    suspend fun insertUser(@Field("email") email: String, @Field("password") password: String, @Field("username") username: String, @Field("nickname") nickname: String, @Field("social_type") social_type: String) : Response<HashMap<String, Any>>

    /**
     * 유저 수정
     * */
//    @PUT("users/{id}")
//    suspend fun updateUser(@Path("id") id: Int, @Body urlencoded :User) : Response<HashMap<String, Any>>
    @FormUrlEncoded
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Field("nickname") nickname: String, @Field("username") username: String) : Response<HashMap<String, Any>>

    /**
     * 로그인
     * */
    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(@Field("email") email : String, @Field("password") password: String) : Response<HashMap<String, Any>>

    /**
     * 유저 탈퇴
     * */
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int) : Response<HashMap<String, Any>>

    /**
     * 이메일 중복체크
     * */
    @GET("users/exist")
    suspend fun doubleCheckEmail(@Query("email") email:String) : Response<HashMap<String, Any>>


    /**
     * 사용자 비밀번호 변경
     */
    @FormUrlEncoded
    @PUT("users/{id}")
    suspend fun updateUserPw(@Path("id") id: Int, @Field("password") password: String) : Response<HashMap<String, Any>>

    /**
     * email, username 으로 사용자 정보 조회 -> 비밀번호 변경 시 사용자 인증
     */
    @GET("users/find")
    suspend fun selectUserByEmailUsername(@Query("email") email: String, @Query("username") username: String) : Response<HashMap<String, Any>>

    /**
     * 사용자 token 정보 update
     */
    @FormUrlEncoded
    @PUT("users/{id}")
    suspend fun updateUserToken(@Path("id") id: Int, @Field("token") token: String) : Response<HashMap<String, Any>>
}