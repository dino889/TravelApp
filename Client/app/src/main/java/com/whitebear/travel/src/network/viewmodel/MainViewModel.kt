package com.whitebear.travel.src.network.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.internal.Mutable
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.whitebear.travel.src.dto.Area
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.network.service.AreaService
import com.whitebear.travel.src.network.service.PlaceService
import com.whitebear.travel.util.CommonUtils
import kotlinx.coroutines.launch

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
    val places : LiveData<MutableList<Place>>
        get() = _places
    private fun setPlace(places:MutableList<Place>) = viewModelScope.launch { 
        _places.value = places
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
}