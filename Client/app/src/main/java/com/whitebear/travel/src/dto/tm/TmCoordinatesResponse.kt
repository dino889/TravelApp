package com.whitebear.travel.src.dto.tm

import com.google.gson.annotations.SerializedName

data class TmCoordinatesResponse (
    @SerializedName("documents")
    val documents: List<Document>?,
    @SerializedName("meta")
    val meta: Meta?
        )

data class Document(
    @SerializedName("x")
    val x: Double?,
    @SerializedName("y")
    val y: Double?
)

data class Meta(
    @SerializedName("total_count")
    val totalCount: Int?
)