package com.example.thelegendofzelda.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Shrine(
    @Json(name = "id") val id: String,
    @Json(name = "name_ko") val nameKo: String,
    @Json(name = "name_en") val nameEn: String,
    @Json(name = "region") val region: String,
    @Json(name = "x") val x: Float,
    @Json(name = "y") val y: Float,
    @Json(name = "tip") val tip: String,
    @Json(name = "is_cleared") val isCleared: Boolean = false
)
