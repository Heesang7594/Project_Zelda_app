package com.example.thelegendofzelda.data.local

import android.content.Context
import com.example.thelegendofzelda.data.model.CompendiumCategoryResponse
import com.example.thelegendofzelda.data.model.CompendiumEntry
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalCompendiumDataSource(
    private val context: Context,
    private val moshi: Moshi
) {
    private var cachedData: List<CompendiumEntry>? = null

    suspend fun getAllEntries(): List<CompendiumEntry> = withContext(Dispatchers.IO) {
        cachedData?.let { return@withContext it }

        try {
            val jsonString = context.assets.open("totk_compendium_ko.json").bufferedReader().use { it.readText() }
            val adapter = moshi.adapter(CompendiumCategoryResponse::class.java)
            val response = adapter.fromJson(jsonString)
            
            val entries = response?.data ?: emptyList()
            cachedData = entries
            entries
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getCategory(category: String): List<CompendiumEntry> {
        val all = getAllEntries()
        return all.filter { it.category == category }
    }

    suspend fun getEntry(idOrName: String): CompendiumEntry? {
        val all = getAllEntries()
        return all.find { it.name == idOrName || it.id.toString() == idOrName }
    }
}
