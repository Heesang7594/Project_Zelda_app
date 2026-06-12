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
import retrofit2.HttpException

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
                        parts = listOf(Part(text = "당신은 사용자에게 도움이 되는 친절한 AI 어시스턴트입니다. 젤다의 전설 관련 질문뿐만 아니라 어떠한 질문에도 자유롭고 친절하게 한국어로 답변해 주세요."))
                    )
                )

                val response = RetrofitClient.geminiApi.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "답변을 생성하지 못했습니다."
                
                _searchState.value = UiState.Success(text)
            } catch (e: HttpException) {
                if (e.code() == 503) {
                    _searchState.value = UiState.Error("현재 AI 서버에 접속자가 많아 일시적인 지연이 발생하고 있습니다. 잠시 후 다시 시도해 주세요.")
                } else {
                    _searchState.value = UiState.Error("네트워크 오류가 발생했습니다 (${e.code()}).")
                }
            } catch (e: Exception) {
                _searchState.value = UiState.Error(e.localizedMessage ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}
