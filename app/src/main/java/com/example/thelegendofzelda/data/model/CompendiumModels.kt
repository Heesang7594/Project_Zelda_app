package com.example.thelegendofzelda.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompendiumCategoryResponse(
    @Json(name = "data") val data: List<CompendiumEntry>
)

@JsonClass(generateAdapter = true)
data class CompendiumEntryResponse(
    @Json(name = "data") val data: CompendiumEntry
)

@JsonClass(generateAdapter = true)
data class CompendiumEntry(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "category") val category: String,
    @Json(name = "description") val description: String,
    @Json(name = "image") val image: String,
    @Json(name = "common_locations") val commonLocations: List<String>? = null,
    @Json(name = "drops") val drops: List<String>? = null,
    @Json(name = "properties") val properties: Properties? = null,
    @Json(name = "name_ko") val nameKo: String? = null,
    @Json(name = "description_ko") val descriptionKo: String? = null,
    @Json(name = "category_ko") val categoryKo: String? = null,
    @Json(name = "common_locations_ko") val commonLocationsKo: List<String>? = null,
    @Json(name = "drops_ko") val dropsKo: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class Properties(
    @Json(name = "attack") val attack: Int? = null,
    @Json(name = "defense") val defense: Int? = null
)
