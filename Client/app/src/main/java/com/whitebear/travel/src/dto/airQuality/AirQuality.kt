package com.whitebear.travel.src.dto.airQuality

import com.google.gson.annotations.SerializedName
import com.whitebear.travel.src.dto.Responses

data class AirQuality (
    @SerializedName("response")
    val response: Responses<Any?>
)