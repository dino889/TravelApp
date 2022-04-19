package com.whitebear.travel.src.dto

data class Route(
    val description: String,
    val heartCount: Int,
    val id: Int,
    val imgURL: String,
    val name: String,
    val placeIdList: String,
    val rating: Int,
    val reivewCnt: Int,
    val type: String
)