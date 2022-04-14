package com.whitebear.travel.src.dto

data class Message2 (
    var isSuccess : Boolean,
    var message: String,
    var data : List<HashMap<String,Any>>
)