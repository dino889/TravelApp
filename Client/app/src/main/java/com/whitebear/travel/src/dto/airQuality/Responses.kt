package com.whitebear.travel.src.dto

import com.google.gson.annotations.SerializedName
import com.whitebear.travel.src.dto.airQuality.Measure

data class Responses (
    @SerializedName("body")
    val body: Body?,
    @SerializedName("header")
    val header: Header?
        )

data class Body(
    @SerializedName("items")
    val measuredValues: List<Measure>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)

data class Header(
    @SerializedName("resultCode")
    val resultCode: String?,
    @SerializedName("resultMsg")
    val resultMsg: String?
)