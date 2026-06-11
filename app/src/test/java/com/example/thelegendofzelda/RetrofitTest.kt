package com.example.thelegendofzelda.data.remote

import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

fun main() {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val geminiApi = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GeminiApi::class.java)

    runBlocking {
        try {
            val req = GeminiRequest(contents = listOf(Content(parts = listOf(Part("test")))))
            geminiApi.generateContent("TEST", req)
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}
