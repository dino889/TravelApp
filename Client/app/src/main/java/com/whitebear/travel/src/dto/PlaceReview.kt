package com.whitebear.travel.src.dto

data class PlaceReview(
    val content: String,
    val createdAt: String,
    val id: Int,
    val placeId: String,
    val rate: String,
    val updatedAt: String,
    val userId: String
)