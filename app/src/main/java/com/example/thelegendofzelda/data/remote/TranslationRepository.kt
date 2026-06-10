package com.example.thelegendofzelda.data.remote

import com.example.thelegendofzelda.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TranslationRepository {
    private val nameCache = mutableMapOf<String, String>()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun translateNames(names: List<String>): Map<String, String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) return@withContext names.associateWith { it }

        val toTranslate = names.filter { !nameCache.containsKey(it) }.distinct()
        if (toTranslate.isEmpty()) {
            return@withContext names.associateWith { nameCache[it] ?: it }
        }

        val prompt = "다음은 젤다의 전설 왕국의 눈물 게임에 등장하는 아이템들의 영문 이름입니다. 공식 한국어 명칭으로 번역해서 JSON 형식(Key: 영문, Value: 한글)으로만 반환해주세요. 백틱이나 마크다운 문법 없이 중괄호로 시작하는 순수 JSON 객체 문자열만 반환하세요.\n\n" + toTranslate.joinToString(", ")
        
        try {
            val request = GeminiRequest(
                contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                systemInstruction = null
            )
            val response = RetrofitClient.geminiApi.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            
            val jsonStart = text.indexOf('{')
            val jsonEnd = text.lastIndexOf('}')
            
            if (jsonStart != -1 && jsonEnd != -1) {
                val jsonString = text.substring(jsonStart, jsonEnd + 1)
                val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
                val mapAdapter = moshi.adapter<Map<String, String>>(type)
                val translatedMap = mapAdapter.fromJson(jsonString) ?: emptyMap()
                
                nameCache.putAll(translatedMap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext names.associateWith { nameCache[it] ?: it }
    }

    suspend fun translateDescription(description: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) return@withContext description

        val prompt = "다음은 젤다의 전설 게임 내 아이템의 상세 설명입니다. 게임 용어를 고려하여 한국어로 자연스럽게 번역해주세요. 추가 설명이나 인사말 없이 번역된 결과만 바로 반환하세요:\n\n$description"
        try {
            val request = GeminiRequest(
                contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                systemInstruction = null
            )
            val response = RetrofitClient.geminiApi.generateContent(apiKey, request)
            return@withContext response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: description
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext description
        }
    }
}
