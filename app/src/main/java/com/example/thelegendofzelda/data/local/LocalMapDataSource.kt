package com.example.thelegendofzelda.data.local

import android.content.Context
import com.example.thelegendofzelda.data.model.Shrine
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalMapDataSource(
    private val context: Context,
    private val moshi: Moshi
) {
    suspend fun getShrines(): List<Shrine> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("totk_shrines_ko.json").bufferedReader().use { it.readText() }
            val listType = Types.newParameterizedType(List::class.java, Shrine::class.java)
            val adapter = moshi.adapter<List<Shrine>>(listType)
            adapter.fromJson(jsonString) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
