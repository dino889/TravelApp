package com.whitebear.travel.src.dto

data class Place(
    val address: String,
    val areaId: Any,
    val createdAt: String,
    val description: String,
    val id: Int,
    val imgURL: String,
    val lat: Double,
    val long: Double,
    val name: String,
    val heartCount: Int,
    val rating:Float,
    val summary: String,
    val distance: Double,
    val type: String,
    val updatedAt: String
)