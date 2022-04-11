package com.whitebear.travel.src.network.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.whitebear.travel.src.dto.Area
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.src.network.service.AreaService
import com.whitebear.travel.src.network.service.PlaceService
import com.whitebear.travel.src.network.service.UserService
import com.whitebear.travel.util.CommonUtils
import kotlinx.coroutines.launch
import java.lang.reflect.Type

private const val TAG = "mainViewModel"
class MainViewModel :ViewModel(){
    /**
     * areaViewModel
     * */
    private val _areas = MutableLiveData<MutableList<Area>>()
    val areas : LiveData<MutableList<Area>>
        get() = _areas
    
    private fun setArea(areas : MutableList<Area>) = viewModelScope.launch { 
        _areas.value = areas
    }
    suspend fun getAreas(){
        val response = AreaService().getArea()
        Log.d(TAG, "getAreas: ${response.code()}")

        viewModelScope.launch { 
            if(response.code() == 200 || response.code() == 500){

                val res = response.body()
                if(res!=null){
                    var type = object:TypeToken<MutableList<Area>>() {}.type
                    var areaList = CommonUtils.parseDto<MutableList<Area>>(res.data,type)
                    var areas = mutableListOf<Area>()
                    for(i in 0..9){
                        areas.add(areaList[i])
                    }
                    setArea(areas)
                }

            }
        }
    }
    /**
     * placeViewModel
     * */
    private val _places = MutableLiveData<MutableList<Place>>()
    private val _place = MutableLiveData<Place>()
    val places : LiveData<MutableList<Place>>
        get() = _places
    val place : LiveData<Place>
        get() = _place

    private fun setPlace(places:MutableList<Place>) = viewModelScope.launch { 
        _places.value = places
    }
    private fun setPlaceOne(place:Place) = viewModelScope.launch {
        _place.value = place
    }
    suspend fun getPlaces(areaName:String){
        val response = PlaceService().getPlaceByArea(areaName)
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500){
                val res = response.body()
                if(res!=null){
                    var type = object : TypeToken<MutableList<Place>>() {}.type
                    var placeList = CommonUtils.parseDto<MutableList<Place>>(res.data,type)
                    setPlace(placeList)
                }
            }
        }
    }
    suspend fun getPlace(id:Int){
        val response = PlaceService().getPlaceById(id)
        viewModelScope.launch {
            if(response.code()==200 || response.code() == 500){
                val res = response.body()
                if(res!=null){
                    var type = object : TypeToken<Place>() {}.type
                    var place = CommonUtils.parseDto<Place>(res.data, type)
                    Log.d(TAG, "getPlace: ${res.data}")
                    setPlaceOne(place)
                }
            }
        }
    }




    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * @author Jiwoo
     * USER ViewModel
     */
    private val _allUserList = MutableLiveData<MutableList<User>>()
    private val _loginUserInfo = MutableLiveData<User>()
    private val _userInfo = MutableLiveData<User>()
    var userId = 0
    private var _userLoc : Location? = null
    private var _userAddr : String? = null

    val allUserList :  LiveData<MutableList<User>>
        get() = _allUserList

    val loginUserInfo : LiveData<User>
        get() = _loginUserInfo

    val userInformation : LiveData<User>
        get() = _userInfo

    val userLoc : Location?
        get() = _userLoc

    val userAddr : String?
        get() = _userAddr

    private fun setLoginUserInfo(user: User) = viewModelScope.launch {
        _loginUserInfo.value = user
    }

    private fun setUserInfo(user: User) = viewModelScope.launch {
        _userInfo.value = user
    }

    private fun setAllUserList(userList : MutableList<User>) = viewModelScope.launch {
        _allUserList.value = userList
    }

    fun setUserLoc(location : Location, addr: String) {
        _userLoc = location
        _userAddr = addr
    }


//    suspend fun getAllUserList() {
//        val response = UserService().selectAllUsers()
//
//        viewModelScope.launch {
//            if(response.code() == 200 || response.code() == 500) {
//                val res = response.body()
//                if(res != null) {
//                    if(res.success) {
//                        if(res.data["user"] != null && res.message == "회원 정보 조회 성공") {
//                            val type = object : TypeToken<MutableList<User>>() {}.type
//                            val userList: MutableList<User> = CommonUtils.parseDto(res.data["user"]!!, type)
//                            setAllUserList(userList)
//                        } else {
//                            Log.e(TAG, "getAllUserList: ${res.message}", )  // 회원 정보 조회 실패
//                        }
//                    } else {
//                        Log.e(TAG, "getAllUserList: 서버 통신 실패 ${res.message}", )
//                    }
//                }
//            }
//        }
//    }

    suspend fun getUserInfo(userId: Int, loginChk : Boolean) : Int {
        var returnRes = -1
        val response = UserService().selectUser(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if(res["isSuccess"] == true) {
                        if(res["data"] != null) {
//                            val type: Type = object : TypeToken<User>() {}.type
//                            val user = CommonUtils.parseDto<User>(res["data"]!!, type)
                            val user = res["data"] as User
                            if(loginChk == true) {  // login user
                                setLoginUserInfo(user)
                            } else {
                                setUserInfo(user)
                            }
                            returnRes = 1
                        } else if(res["data"] == null) {
                            returnRes = 2   // 탈퇴한 회원 정보 조회 또는 에러
                        }
                    } else {
                        returnRes = -1  // 서버 통신 오류
                        Log.e(TAG, "getUserInfo: ${res["message"]}")
                    }

                } else {
                    Log.d(TAG, "getUserInfoError: ${response.message()}")
                }
            }
        }
        return returnRes
    }

    suspend fun join(user: User) : Boolean {
        var result = false
        val response = UserService().insertUser(user)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if(res["isSuccess"] == true && res["message"] == "create user successful") {
                        val type: Type = object : TypeToken<User>() {}.type
                        val user = CommonUtils.parseDto<User>(res["data"]!!, type)
                        Log.d(TAG, "join: $user")
                        result = true
                    } else {
                        result = false
                        Log.d(TAG, "joinError: ${response.message()}")
                    }
                }
            }
        }
        return result
    }

    suspend fun login(email: String, password: String) : HashMap<String, Any> {
        var result : HashMap<String, Any> = hashMapOf()
        val response = UserService().login(email, password)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
//                    if(res["isSuccess"] == true && res["message"] == "login success") {
//                        val user = res["data"] as User
//                        Log.d(TAG, "login: $user")
//                        result = user
//                    } else {
//                        result = null
//                        Log.d(TAG, "loginError: ${response.message()}")
//                    }
                }
            }
        }
        return result
    }

    suspend fun existsChkUserEmail(email: String) : HashMap<String, Any> {
        var result : HashMap<String, Any> = hashMapOf()
        val response = UserService().doubleCheckEmail(email)

        viewModelScope.launch {
            val res = response.body()
            if(res != null) {
                result = res
            }
        }
        return result
    }
}