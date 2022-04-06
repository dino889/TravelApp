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
    val rating: Int,
    val summary: String,
    val type: Any,
    val updatedAt: String
)