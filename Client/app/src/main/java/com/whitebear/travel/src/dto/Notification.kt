package com.whitebear.travel.src.dto

data class Notification(
    val body: String,
    val createdAt: String,
    val id: Int,
    val title: String,
    val token: String,
    val type: String,
    val updatedAt: String,
    val userId: Int
)