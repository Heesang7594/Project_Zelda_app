package com.example.thelegendofzelda.data.remote

import com.example.thelegendofzelda.data.model.CompendiumCategoryResponse
import com.example.thelegendofzelda.data.model.CompendiumEntryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HyruleCompendiumApi {

    @GET("api/v3/compendium/category/{category}")
    suspend fun getCategory(
        @Path("category") category: String,
        @Query("game") game: String = "totk"
    ): CompendiumCategoryResponse

    @GET("api/v3/compendium/entry/{entry}")
    suspend fun getEntry(
        @Path("entry") entry: String,
        @Query("game") game: String = "totk"
    ): CompendiumEntryResponse
}
