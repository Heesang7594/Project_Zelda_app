package com.example.thelegendofzelda.presentation.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thelegendofzelda.BuildConfig
import com.example.thelegendofzelda.data.remote.RetrofitClient
import com.example.thelegendofzelda.data.remote.YouTubeSearchItem
import com.example.thelegendofzelda.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GuideViewModel : ViewModel() {
    private val _videoState = MutableStateFlow<UiState<List<YouTubeSearchItem>>>(UiState.Loading)
    val videoState: StateFlow<UiState<List<YouTubeSearchItem>>> = _videoState.asStateFlow()

    init {
        searchVideos("젤다 왕국의 눈물 공략")
    }

    fun searchVideos(query: String) {
        viewModelScope.launch {
            _videoState.value = UiState.Loading
            try {
                val apiKey = BuildConfig.YOUTUBE_API_KEY
                if (apiKey.isBlank()) {
                    _videoState.value = UiState.Error("YouTube API Key가 설정되지 않았습니다.")
                    return@launch
                }
                val fullQuery = "젤다의 전설 왕국의 눈물 $query"
                val response = RetrofitClient.youtubeApi.searchVideos(query = fullQuery, apiKey = apiKey)
                _videoState.value = UiState.Success(response.items)
            } catch (e: Exception) {
                _videoState.value = UiState.Error(e.localizedMessage ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}
