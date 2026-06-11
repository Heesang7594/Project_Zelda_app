package com.example.thelegendofzelda.presentation.compendium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thelegendofzelda.data.model.CompendiumEntry
import com.example.thelegendofzelda.data.remote.RetrofitClient
import com.example.thelegendofzelda.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompendiumViewModel : ViewModel() {
    private val _creaturesState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val creaturesState: StateFlow<UiState<List<CompendiumEntry>>> = _creaturesState.asStateFlow()

    private val _monstersState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val monstersState: StateFlow<UiState<List<CompendiumEntry>>> = _monstersState.asStateFlow()

    private val _equipmentState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val equipmentState: StateFlow<UiState<List<CompendiumEntry>>> = _equipmentState.asStateFlow()

    private val _materialsState = MutableStateFlow<UiState<List<CompendiumEntry>>>(UiState.Loading)
    val materialsState: StateFlow<UiState<List<CompendiumEntry>>> = _materialsState.asStateFlow()

    private val isFetchingMap = mutableMapOf<String, Boolean>()

    fun loadCategoryIfNotLoaded(category: String) {
        val stateFlow = when (category) {
            "creatures" -> _creaturesState
            "monsters" -> _monstersState
            "equipment" -> _equipmentState
            "materials" -> _materialsState
            else -> return
        }

        // 이미 로딩 성공한 데이터가 있거나, 현재 로딩 중이라면 중복 호출 방지
        if (stateFlow.value is UiState.Success || isFetchingMap[category] == true) {
            return
        }

        fetchCategory(category, stateFlow)
    }

    private fun fetchCategory(
        category: String,
        stateFlow: MutableStateFlow<UiState<List<CompendiumEntry>>>
    ) {
        viewModelScope.launch {
            isFetchingMap[category] = true
            stateFlow.value = UiState.Loading
            
            try {
                val response = RetrofitClient.hyruleApi.getCategory(category)
                // 영문 원본 데이터 그대로 노출 (번역 API 호출 제거로 안정성 및 속도 극대화)
                stateFlow.value = UiState.Success(response.data)
            } catch (e: Exception) {
                stateFlow.value = UiState.Error(e.localizedMessage ?: "데이터를 불러오는 중 오류가 발생했습니다.")
            } finally {
                isFetchingMap[category] = false
            }
        }
    }
}
