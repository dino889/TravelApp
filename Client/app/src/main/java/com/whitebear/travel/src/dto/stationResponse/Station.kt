package com.whitebear.travel.src.dto.stationResponse

import com.google.gson.annotations.SerializedName

data class StationResponse (
    @SerializedName("response")
    val response: Responses?
        )

data class Station (
    @SerializedName("addr")
    val addr: String?,
    @SerializedName("stationName")
    val stationName: String?,
    @SerializedName("tm")
    val tm: Double?
        )