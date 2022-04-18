package com.whitebear.travel.src.dto

data class Keyword (
    var keyword:String,
    var location:String,
    var curDate:String
        ){
    constructor() : this("","","")
}