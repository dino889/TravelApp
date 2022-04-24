package com.whitebear.travel.src.dto

data class RouteReview(
    val content: String,
    val createdAt: String,
    val id: Int,
    val placeListId: String,
    val rate: String,
    val updatedAt: String,
    val userId: String
)