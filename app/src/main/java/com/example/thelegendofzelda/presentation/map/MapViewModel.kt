package com.example.thelegendofzelda.presentation.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thelegendofzelda.data.local.LocalMapDataSource
import com.example.thelegendofzelda.data.model.Shrine
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ShrineFilter {
    ALL, CLEARED, UNCLEARED
}

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val localDataSource = LocalMapDataSource(application, moshi)

    private val _allShrines = MutableStateFlow<List<Shrine>>(emptyList())
    
    private val _currentFilter = MutableStateFlow(ShrineFilter.ALL)
    val currentFilter: StateFlow<ShrineFilter> = _currentFilter.asStateFlow()

    private val _filteredShrines = MutableStateFlow<List<Shrine>>(emptyList())
    val filteredShrines: StateFlow<List<Shrine>> = _filteredShrines.asStateFlow()

    init {
        loadShrines()
    }

    private fun loadShrines() {
        viewModelScope.launch {
            val shrines = localDataSource.getShrines()
            _allShrines.value = shrines
            applyFilter()
        }
    }

    fun setFilter(filter: ShrineFilter) {
        _currentFilter.value = filter
        applyFilter()
    }

    fun toggleShrineCleared(shrineId: String) {
        val updatedList = _allShrines.value.map {
            if (it.id == shrineId) it.copy(isCleared = !it.isCleared) else it
        }
        _allShrines.value = updatedList
        applyFilter()
    }

    private fun applyFilter() {
        val all = _allShrines.value
        val filtered = when (_currentFilter.value) {
            ShrineFilter.ALL -> all
            ShrineFilter.CLEARED -> all.filter { it.isCleared }
            ShrineFilter.UNCLEARED -> all.filter { !it.isCleared }
        }
        _filteredShrines.value = filtered
    }
}
