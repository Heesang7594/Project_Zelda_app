package com.example.thelegendofzelda.presentation.ai_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thelegendofzelda.BuildConfig
import com.example.thelegendofzelda.data.remote.Content
import com.example.thelegendofzelda.data.remote.GeminiRequest
import com.example.thelegendofzelda.data.remote.Part
import com.example.thelegendofzelda.data.remote.RetrofitClient
import com.example.thelegendofzelda.data.remote.SystemInstruction
import com.example.thelegendofzelda.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiSearchViewModel : ViewModel() {
    private val _searchState = MutableStateFlow<UiState<String>>(UiState.Success(""))
    val searchState: StateFlow<UiState<String>> = _searchState.asStateFlow()

    fun generateContent(query: String) {
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isBlank()) {
                    _searchState.value = UiState.Error("Gemini API Key가 설정되지 않았습니다.")
                    return@launch
                }

                val request = GeminiRequest(
                    contents = listOf(Content(role = "user", parts = listOf(Part(text = query)))),
                    systemInstruction = SystemInstruction(
                        parts = listOf(Part(text = "당신은 '젤다의 전설: 왕국의 눈물'의 공식 게임 가이드입니다. 정확한 아이템 위치와 공략 정보만을 제공해야 하며, 게임과 관련 없거나 거짓된 정보는 답변하지 마세요."))
                    )
                )

                val response = RetrofitClient.geminiApi.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "답변을 생성하지 못했습니다."
                
                _searchState.value = UiState.Success(text)
            } catch (e: Exception) {
                _searchState.value = UiState.Error(e.localizedMessage ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}
