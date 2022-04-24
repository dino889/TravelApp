package com.whitebear.travel.src.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NidProfile(
    @SerializedName("age") val age: String,
    @SerializedName("birthYear") val birthYear: String,
    @SerializedName("birthday") val birthday: String,
    @SerializedName("email") val email: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("id") val id: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("name") val name: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String
) : Serializable