package com.example.thelegendofzelda.presentation.ai_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thelegendofzelda.util.UiState

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSearchScreen(viewModel: AiSearchViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<ChatMessage>()) }
    
    val searchState by viewModel.searchState.collectAsState()

    LaunchedEffect(Unit) {
        chatHistory = listOf(
            ChatMessage("안녕하세요! 젤다 게임 공략 도우미입니다. 궁금한 것을 물어보세요.\n예: '추운 지역 탐험 시 필요한 방한복이나 요리 레시피 알려줘'", false)
        )
    }

    LaunchedEffect(searchState) {
        when (val state = searchState) {
            is UiState.Success -> {
                if (state.data.isNotEmpty()) {
                    chatHistory = chatHistory + ChatMessage(state.data, false)
                }
            }
            is UiState.Error -> {
                chatHistory = chatHistory + ChatMessage(state.message, false)
            }
            UiState.Loading -> { }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("✨ AI 검색\nGemini API 기반 분석") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatHistory) { msg ->
                    ChatBubble(msg)
                }
                if (searchState is UiState.Loading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("질문을 입력하세요...") },
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (query.isNotBlank()) {
                            chatHistory = chatHistory + ChatMessage(query, true)
                            viewModel.generateContent(query)
                            query = ""
                        }
                    },
                    modifier = Modifier.background(Color.Gray, RoundedCornerShape(24.dp))
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isUser) Color(0xFF333333) else Color(0xFFF0F0F0)
    val textColor = if (message.isUser) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Surface(
            color = color,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
