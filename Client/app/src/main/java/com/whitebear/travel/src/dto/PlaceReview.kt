package com.whitebear.travel.src.dto

data class PlaceReview(
    val content: String,
    val createdAt: String,
    val id: Int,
    val placeId: Int,
    val rate: Int,
    val updatedAt: String,
    val userId: Int,
    val user:User?
){
    constructor(content: String,placeId: Int,rate: Int,userId: Int):this(content, "0", 0, placeId, rate, "", userId, null)
}