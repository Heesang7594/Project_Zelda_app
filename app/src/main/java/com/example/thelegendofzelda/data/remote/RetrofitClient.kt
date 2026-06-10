package com.example.thelegendofzelda.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val hyruleApi: HyruleCompendiumApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.hyrule-compendium.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(HyruleCompendiumApi::class.java)
    }

    val geminiApi: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    val youtubeApi: YouTubeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YouTubeApi::class.java)
    }
}
