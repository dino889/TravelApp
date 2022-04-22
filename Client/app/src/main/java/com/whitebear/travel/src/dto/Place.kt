package com.whitebear.travel.src.dto

data class Place(
    val address: String,
    val areaId: Int,
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
){
    constructor(address:String,
                id:Int,
                imgURL: String,
                lat: Double,
                long: Double,
                name: String,
                summary: String):this(address, 0, "", "", id, imgURL, lat, long, name, 0, 0f, summary, 0.0, "", "")
}