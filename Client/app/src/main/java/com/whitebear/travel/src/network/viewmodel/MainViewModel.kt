package com.whitebear.travel.src.network.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.whitebear.travel.src.dto.*
import com.whitebear.travel.src.dto.airQuality.AirQuality
import com.whitebear.travel.src.dto.airQuality.Measure
import com.whitebear.travel.src.dto.camping.Item
import com.whitebear.travel.src.dto.stationResponse.StationResponse
import com.whitebear.travel.src.dto.tm.TmCoordinatesResponse
import com.whitebear.travel.src.network.service.AreaService
import com.whitebear.travel.src.network.service.PlaceService
import com.whitebear.travel.src.network.service.UserService
import com.whitebear.travel.src.network.service.DataService
import com.whitebear.travel.src.network.service.*
import com.whitebear.travel.util.CommonUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Type
import kotlin.collections.HashMap
import kotlin.math.log

private const val TAG = "mainViewModel"
class MainViewModel :ViewModel(){
    /**
     * Weather ViewModel
     * */
    val userLoc : LiveData<Location>
        get() = _userLoc
    val userAddr : String?
        get() = _userAddr
    val today : Int?
        get() = _today
    val hour : String?
        get() = _hour

    private var _userLoc = MutableLiveData<Location>()
    private var _userAddr : String? = null
    private var _today : Int? = null
    private var _hour : String? = null

    fun setUserLoc(location : Location, addr: String) {
        _userLoc.value = location
        _userAddr = addr
    }
    fun setHour(hour:String){
        _hour = hour
    }

    fun setToday(today: Int){
        _today = today
    }

    private val _weathers = MutableLiveData<Weather>()
    private val _measures = MutableLiveData<Measure>()
    private val _coordinates = MutableLiveData<TmCoordinatesResponse>()
    private val _stations = MutableLiveData<StationResponse>()
    private val _air = MutableLiveData<AirQuality>()

    val weathers : LiveData<Weather>
        get() = _weathers
    val measures : LiveData<Measure>
        get() = _measures
    val coordinates : LiveData<TmCoordinatesResponse>
        get() = _coordinates
    val stations : LiveData<StationResponse>
        get() = _stations
    val air : LiveData<AirQuality>
        get() = _air

    private fun setWeather(weather: Weather) = viewModelScope.launch {
        _weathers.value = weather
    }
    private fun setMeasure(measure: Measure) = viewModelScope.launch {
        _measures.value = measure
    }
    private fun setTm(coordinates:TmCoordinatesResponse) = viewModelScope.launch {
        _coordinates.value = coordinates
    }
    private fun setStation(station:StationResponse) = viewModelScope.launch {
        _stations.value = station
    }
    private fun setAir(air:AirQuality) = viewModelScope.launch {
        _air.value = air
    }
    suspend fun getWeather(dataType : String, numOfRows : Int, pageNo : Int,
                           baseDate : Int, baseTime : String, nx : String, ny : String){

        val response = DataService().getWeather(dataType, numOfRows, pageNo, baseDate, baseTime, nx, ny)
        Log.d(TAG, "getWeather: ${response.code()}")
        viewModelScope.launch { 
            if(response.code() == 200){
                var res = response.body()
                if(res != null) {
                    var type = object:TypeToken<Weather>() {}.type
                    var weatherList = CommonUtils.parseDto<Weather>(res ,type)
                    if (weatherList != null) {
                        setWeather(weatherList)
                    }
                } else {
                    Log.e(TAG, "getWeather: ${response.message()}", )
                }
            }
            
        }
    }
    suspend fun getNearbyCenter(lat:Double, lng:Double){
        val response = DataService().getTmCoordinates(lng, lat)
        Log.d(TAG, "getNearbyCenter: ${response.code()}")
        if(response.code() == 200){
            var res = response.body()
            if(res!=null){
                var type = object:TypeToken<TmCoordinatesResponse>() {}.type
                var tmCoordinate = CommonUtils.parseDto<TmCoordinatesResponse>(res,type)
                setTm(tmCoordinate)
            }
        }
    }
    suspend fun getFindMyCenter(lat:Double, lng:Double) {
        val response = DataService().getMeasure(lat,lng)
        if(response.code() == 200){
            var res = response.body()
            if(res!=null){
                var type = object : TypeToken<StationResponse>() {}.type
                var station = CommonUtils.parseDto<StationResponse>(res,type)
                setStation(station)
            }
        }
    }
    
    suspend fun getAirQuality(stationName:String){
        val response = DataService().getAirQuality(stationName)

        if(response.code() == 200){
            var res = response.body()
            if(res!=null){
                var type = object : TypeToken<AirQuality>() {}.type
                var airQuality = CommonUtils.parseDto<AirQuality>(res,type)
                setAir(airQuality)
            }
        }
    }
    /**
     * areaViewModel
     * */

    private val _areas = MutableLiveData<MutableList<Area>>()
    private val _categorys = MutableLiveData<MutableList<String>>()
    private val _area = MutableLiveData<Area>()

    val areas : LiveData<MutableList<Area>>
        get() = _areas
    val area : LiveData<Area>
        get() = _area
    val categorys : LiveData<MutableList<String>>
        get() = _categorys

    private fun setArea(areas : MutableList<Area>) = viewModelScope.launch { 
        _areas.value = areas
    }
    private fun setAreaOne(area:Area) = viewModelScope.launch {
        _area.value = area
    }
    private fun setCategorys(categorys : MutableList<String>) = viewModelScope.launch {
        _categorys.value = categorys
    }

    suspend fun getAreas(){
        val response = AreaService().getArea()
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
    fun getAreaOne(areaId:Int){
        for(item in 0..areas.value!!.size-1){
            if(areas.value!![item].id == areaId){
                setAreaOne(areas.value!![item])
                break;
            }
        }
    }
    suspend fun getCategorys(){
        val response = AreaService().getCategory()
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500){

                val res = response.body()
                if(res!=null){
                    var type = object:TypeToken<MutableList<String>>() {}.type
                    var categoryList = CommonUtils.parseDto<MutableList<String>>(res.data,type)
                    setCategorys(categoryList)
                }

            }
        }
    }
    /**
     * placeViewModel
     * */
    private val _places = MutableLiveData<MutableList<Place>>()
    private val _place = MutableLiveData<Place>()
    private val _placeReviews = MutableLiveData<MutableList<PlaceReview>>()
    private val _placeLikes = MutableLiveData<MutableList<Place>>()

    val places : LiveData<MutableList<Place>>
        get() = _places
    val place : LiveData<Place>
        get() = _place
    val placeReviews : LiveData<MutableList<PlaceReview>>
        get() = _placeReviews
    val placeLikes : LiveData<MutableList<Place>>
        get() = _placeLikes

    private fun setPlace(places:MutableList<Place>) = viewModelScope.launch { 
        _places.value = places
    }
    private fun setPlaceOne(place:Place) = viewModelScope.launch {
        _place.value = place
    }
    private fun setPlaceReview(placeReviews: MutableList<PlaceReview>) = viewModelScope.launch {
        _placeReviews.value = placeReviews
    }
    private fun setPlaceLikes(places:MutableList<Place>) = viewModelScope.launch {
        _placeLikes.value = places
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
    suspend fun getPlacesToSort(areaName: String, sort:String){
        val response = PlaceService().getPlaceByAreaSorting(areaName,sort)
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
                    setPlaceOne(place)
                }
            }
        }
    }
    suspend fun getPlaceReview(placeId:Int){
        val response = PlaceService().getPlaceReview(placeId)
        viewModelScope.launch {
            if(response.code()==200 || response.code() == 500){
                val res = response.body()
                if(res!=null){
                    var type = object : TypeToken<MutableList<PlaceReview>>() {}.type
                    var placeReview = CommonUtils.parseDto<MutableList<PlaceReview>>(res.data, type)
                    setPlaceReview(placeReview)
                }
            }else if(response.code() == 201){
                val res = response.body()
                if(res!=null){
                    var type = object : TypeToken<MutableList<PlaceReview>>() {}.type
                    var placeReview = CommonUtils.parseDto<MutableList<PlaceReview>>(res.data, type)
                    setPlaceReview(placeReview)
                }else{
                    var nullsList = mutableListOf<PlaceReview>()
                    setPlaceReview(nullsList)
                }

            }
        }
    }
    suspend fun getPlaceLikes(userId: Int){
        val response = PlaceService().getLikePlaceByUser(userId)
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500 || response.code() == 201){
                val res = response.body()

                if(res!=null){
                    if(res.isSuccess){
                        var places = mutableListOf<Place>()
                        Log.d(TAG, "getPlaceLikes: $res")
                        for(i in 0..res.data.size-1){
                            var type = object : TypeToken<Place>() {}.type
                            var place:Place = CommonUtils.parseDto(res.data[i].get("place")!!, type)
                            places.add(place)
                        }
                        setPlaceLikes(places)
                    }
                }
            }
        }
    }

    private val _placesByGps = MutableLiveData<MutableList<Place>>()

    val placesByGps :  LiveData<MutableList<Place>>
        get() = _placesByGps

    private fun setPlacesByGps(placeList: MutableList<Place>) = viewModelScope.launch {
        _placesByGps.value = placeList
    }

    suspend fun getPlacesByGps(lat: Double, long: Double, range: Double) {
        val response = PlaceService().getPlacesByGps(lat, long, range)
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res["isSuccess"] == true && res["data"] != null) {
                        val type = object : TypeToken<MutableList<Place>>() {}.type
                        val placeList = CommonUtils.parseDto<MutableList<Place>>(res["data"]!!,type)
                        setPlacesByGps(placeList)
                    }
                }
            } else {
                Log.e(TAG, "getPlacesByGps: ${response.message()}", )
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

    val allUserList :  LiveData<MutableList<User>>
        get() = _allUserList

    val loginUserInfo : LiveData<User>
        get() = _loginUserInfo

    val userInformation : LiveData<User>
        get() = _userInfo


    private fun setLoginUserInfo(user: User) = viewModelScope.launch {
        _loginUserInfo.value = user
    }

    private fun setUserInfo(user: User) = viewModelScope.launch {
        _userInfo.value = user
    }

    private fun setAllUserList(userList : MutableList<User>) = viewModelScope.launch {
        _allUserList.value = userList
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
                            val type: Type = object : TypeToken<User>() {}.type
                            val user = CommonUtils.parseDto<User>(res["data"]!!, type)
//                            val user = res["data"]
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

    suspend fun join(user: User) : HashMap<String, Any> {
        var result : HashMap<String, Any> = hashMapOf()
        val response = UserService().insertUser(user)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
//                    if(res["isSuccess"] == true && res["message"] == "create user successful") {
//                        val type: Type = object : TypeToken<User>() {}.type
//                        val user = CommonUtils.parseDto<User>(res["data"]!!, type)
//                        Log.d(TAG, "join: $user")
//                        result = user
//                    } else {
//                        result = null
//                        Log.d(TAG, "joinError: ${response.message()}")
//                    }
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
            if(response.code() == 200 || response.code() == 500) {
                if (res != null) {
                    result = res
                }
            }
        }
        return result
    }

    /**
     * Bucket ViewModel
     * */
    private val placeShopResponse = mutableListOf<Place>()
    val liveNavBucketList = MutableLiveData<MutableList<Place>>().apply {
        value = placeShopResponse
    }
    fun insertPlaceShopList(place:Place){
        if(liveNavBucketList.value!!.size < 4){
            placeShopResponse.add(place)
            liveNavBucketList.value = placeShopResponse
        }
    }
    fun removePlaceShopList(placeId:Int){
        for(i in 0..placeShopResponse.size -1 ){
            if(placeShopResponse.get(i).id == placeId){
                placeShopResponse.removeAt(i)
                break
            }
        }
        liveNavBucketList.value = placeShopResponse
    }
    /**
     * searchHistory ViewModel
     * */
    private val keywords = mutableSetOf<Keyword>()
    var sets = mutableSetOf<Keyword>()
//    var hashs = <String,String>()
    val liveKeywords = MutableLiveData<MutableSet<Keyword>>().apply {
        value = keywords
    }
    fun insertKeywords(keyword:Keyword){
        keywords.add(keyword)
        liveKeywords.value = keywords
    }

    /**
     * Route ViewModel
     * */
    private val _routes = MutableLiveData<MutableList<Route>>()
    private val _routesLikes = MutableLiveData<MutableList<Route>>()
    private val _route = MutableLiveData<Route>()
    private val _placeToRoutes = MutableLiveData<MutableList<Place>>()

    val routes : LiveData<MutableList<Route>>
        get() = _routes
    val routesLikes : LiveData<MutableList<Route>>
        get() = _routesLikes
    val route : LiveData<Route>
        get() = _route
    val placesToRoutes : LiveData<MutableList<Place>>
        get() = _placeToRoutes

    private fun setRoutes(routes: MutableList<Route>) = viewModelScope.launch {
        _routes.value = routes
    }
    private fun setRoutesLikes(routes:MutableList<Route>) = viewModelScope.launch {
        _routesLikes.value = routes
    }
    private fun setRoute(route:Route) = viewModelScope.launch {
        _route.value = route
    }
    private fun setPlaceToRoute(places:MutableList<Place>) = viewModelScope.launch {
        _placeToRoutes.value = places
    }

    suspend fun getRoutes(areaName:String){
        val response = RouteService().getRouteByArea(areaName)
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500){
                val res = response.body()
                if(res!=null){
                    var type = object : TypeToken<MutableList<Route>>() {}.type
                    var routeList = CommonUtils.parseDto<MutableList<Route>>(res.data,type)
                    Log.d(TAG, "getRoutes: $routeList")
                    setRoutes(routeList)
                }
            }
        }
    }
    suspend fun getRoutesToSort(areaName: String, sort:String){
        val response = RouteService().getRouteByAreaToSort(areaName,sort)
        Log.d(TAG, "getPlacesToSort: SORT $sort")
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500){
                val res = response.body()
                if(res!=null){
                    var type = object : TypeToken<MutableList<Route>>() {}.type
                    var routeList = CommonUtils.parseDto<MutableList<Route>>(res.data,type)
                    setRoutes(routeList)
                }
            }
        }
    }

    suspend fun getRoutesLikes(userId: Int){
        val response = RouteService().getRouteLikeByUser(userId)
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500 || response.code() == 201){
                val res = response.body()
                if(res!=null){
                    if(res.isSuccess){
                        Log.d(TAG, "getRoutesLikes: ${res.data}")
                        if(res.data.isNotEmpty()) {
                            Log.d(TAG, "getRoutesLikes: ${res.data[0].get("place_list")}")
                            var routes = mutableListOf<Route>()
                            for(i in 0..res.data.size - 1){
                                var type = object : TypeToken<Route>() {}.type
                                var route:Route = CommonUtils.parseDto(res.data[i].get("place_list")!!, type)
                                routes.add(route)
                            }
                            setRoutesLikes(routes)
                        }
                    }
                }
            }
        }
    }
    fun getRoute(id:Int){
        Log.d(TAG, "getRoute: $id")
        var routes = routes.value!!
        for(item in routes){
            if(item.id == id){
                Log.d(TAG, "getRoute: $item")
                setRoute(item)
            }
        }
    }
    suspend fun getRoutesInPlaceArr(arr:String){
        val response = RouteService().getRoutesInPlaceArr(arr)
        Log.d(TAG, "getRoutesLikes: ${response.code()}")
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500 || response.code() == 201){
                val res = response.body()
                if(res!=null){
                    if(res.isSuccess){
                        var type = object : TypeToken<MutableList<Place>>() {}.type
                        var route:MutableList<Place> = CommonUtils.parseDto(res.data, type)
                        Log.d(TAG, "getRoutesInPlaceArr: ${res.data}")
                        setPlaceToRoute(route)
                    }
                }
            }
        }
    }

    /**
     * api - camping 정보
     */
    private val _campingList = MutableLiveData<MutableList<Item>>()

    val campingList :  LiveData<MutableList<Item>>
        get() = _campingList

    private fun setCampingList(list: MutableList<Item>) = viewModelScope.launch {
        _campingList.value = list
    }

    suspend fun getCampingList(lat: Double, long: Double, radius: Int) {   // mapX - Longitude, mapY - Latitude
        val response = DataService().getCamping(long, lat, radius)
        Log.d(TAG, "getCampingList: $response")
        viewModelScope.launch {
            if(response.code() == 200 || response.code() == 500) {
                val res = response.body()
                if(res != null) {
                    if(res.response.header.resultMsg == "OK") {
                        Log.d(TAG, "getCampingList: ${res.response.body.totalCount}")
//                        val item = res.response.body.items.item.item
//                        Log.d(TAG, "getCampingList: ${item[item.size - 1]}")
                    }
                }
            } else {
                Log.e(TAG, "getPlacesByGps: ${response.message()}", )
            }

        }
    }


}