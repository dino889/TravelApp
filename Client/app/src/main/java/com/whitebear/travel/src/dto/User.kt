package com.whitebear.travel.src.dto

data class User(
    val id: Int,
    val email: String,
    var password: String,
    val nickname: String,
    val username: String,
    val social_type: String,
    val token: String,
    val createdAt: String,
    val updatedAt: String
){
    constructor() : this(0, "", "", "", "", "", "", "", "")
    constructor(id: Int) : this(id, "", "", "", "", "", "", "", "")
    constructor(id: Int, token: String) : this(id, "", "", "", "", "", token, "", "")
    constructor(email: String, password: String) : this(0, email, "", "", password, "", "", "", "")
    constructor(email: String, password: String, username: String, nickname: String, social_type: String) : this(id = 0, email = email, password = password, username = username, nickname = nickname, social_type = social_type, token = "", createdAt = "", updatedAt = "")

}