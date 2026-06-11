package com.example.thelegendofzelda.presentation.compendium

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thelegendofzelda.data.local.LocalCompendiumDataSource
import com.example.thelegendofzelda.data.model.CompendiumEntry
import com.example.thelegendofzelda.data.remote.RetrofitClient
import com.example.thelegendofzelda.util.UiState
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompendiumViewModel(application: Application) : AndroidViewModel(application) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val localDataSource = LocalCompendiumDataSource(application, moshi)
    private val _creaturesState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val creaturesState: StateFlow<UiState<List<CompendiumEntry>>> = _creaturesState.asStateFlow()

    private val _monstersState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val monstersState: StateFlow<UiState<List<CompendiumEntry>>> = _monstersState.asStateFlow()

    private val _equipmentState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val equipmentState: StateFlow<UiState<List<CompendiumEntry>>> = _equipmentState.asStateFlow()

    private val _materialsState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val materialsState: StateFlow<UiState<List<CompendiumEntry>>> = _materialsState.asStateFlow()

    init {
        fetchCategory("creatures", _creaturesState)
        fetchCategory("monsters", _monstersState)
        fetchCategory("equipment", _equipmentState)
        fetchCategory("materials", _materialsState)
    }

    private fun fetchCategory(
        category: String,
        stateFlow: MutableStateFlow<UiState<List<CompendiumEntry>>>
    ) {
        viewModelScope.launch {
            stateFlow.value = UiState.Loading
            try {
                // Fetch directly from our local JSON data source which contains Korean translations!
                val entries = localDataSource.getCategory(category)
                
                stateFlow.value = UiState.Success(entries)
            } catch (e: Exception) {
                stateFlow.value = UiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }
}
