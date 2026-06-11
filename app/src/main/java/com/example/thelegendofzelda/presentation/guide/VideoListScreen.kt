package com.example.thelegendofzelda.presentation.guide

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.thelegendofzelda.data.remote.YouTubeSearchItem
import com.example.thelegendofzelda.util.UiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(navController: androidx.navigation.NavController, viewModel: GuideViewModel = viewModel()) {
    val videoState by viewModel.videoState.collectAsState()
    val context = LocalContext.current
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("전체", "보스", "사당", "전투", "팁")

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex in tabs.indices) {
            val query = if (selectedTabIndex == 0) "공략" else tabs[selectedTabIndex] + " 공략"
            viewModel.searchVideos(query)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("▶ 유튜브 공략 영상") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE50914), // Youtube red
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            var searchQuery by remember { mutableStateOf("") }
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("영상 검색 (예: 마스터 소드)") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotBlank()) {
                            selectedTabIndex = -1 // No chip selected
                            viewModel.searchVideos(searchQuery)
                        }
                    }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                },
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            )

            // Quick Filters
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs.size) { index ->
                    FilterChip(
                        selected = selectedTabIndex == index,
                        onClick = { 
                            selectedTabIndex = index
                            searchQuery = "" 
                        },
                        label = { Text(tabs[index]) }
                    )
                }
            }

            when (val state = videoState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.data) { item ->
                            VideoItemCard(item = item) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${item.id.videoId}"))
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItemCard(item: YouTubeSearchItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.snippet.thumbnails.high.url,
                contentDescription = item.snippet.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(120.dp, 90.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = item.snippet.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "YouTube", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}
