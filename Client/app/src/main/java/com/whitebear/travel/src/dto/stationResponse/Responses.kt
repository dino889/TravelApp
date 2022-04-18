package com.whitebear.travel.src.dto.stationResponse

import com.google.gson.annotations.SerializedName

data class Responses (
    @SerializedName("body")
    val body: Body?,
    @SerializedName("header")
    val header: Header?
        )

data class Body(
    @SerializedName("items")
    val stations: List<Station>?,
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