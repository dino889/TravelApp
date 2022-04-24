package com.whitebear.travel.src.dto.corona

data class Quarantine(
    val countryName: String,
    val death: String,
    val newCase: String,
    val newCcase: String,
    val newFcase: String,
    val percentage: String,
    val recovered: String,
    val totalCase: String
)