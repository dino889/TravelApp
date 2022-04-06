package com.whitebear.travel.src.dto

data class User(
    val createdAt: String,
    val email: String,
    val id: Int,
    val nickname: String,
    val password: String,
    val social_type: String,
    val updatedAt: String,
    val username: String
){
    constructor(email: String, password: String) : this("", email, 0, "", password, "", "", "")

}