package com.example.thelegendofzelda.presentation.compendium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thelegendofzelda.data.model.CompendiumEntry
import com.example.thelegendofzelda.data.remote.RetrofitClient
import com.example.thelegendofzelda.data.remote.TranslationRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ItemDetailScreen(navController: NavController, category: String, id: Int) {
    var entry by remember { mutableStateOf<CompendiumEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var translatedDescription by remember { mutableStateOf<String?>(null) }
    var isTranslating by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        try {
            val response = RetrofitClient.hyruleApi.getEntry(id.toString())
            var item = response.data
            
            val tName = TranslationRepository.translateNames(listOf(item.name))[item.name] ?: item.name
            item = item.copy(name = tName)
            entry = item

            isTranslating = true
            translatedDescription = TranslationRepository.translateDescription(item.description)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
        } finally {
            isLoading = false
            isTranslating = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entry?.name?.replaceFirstChar { it.uppercase() } ?: "상세 정보") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else {
                entry?.let { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        AsyncImage(
                            model = item.image,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("ⓘ 기본 정보", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("이름: ${item.name}")
                                Text("분류: ${item.category}")
                                Text("도감 번호: #${item.id}")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!item.commonLocations.isNullOrEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("📍 주요 서식지 및 획득처", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    item.commonLocations.forEach { loc ->
                                        Text("• $loc")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        if (!item.drops.isNullOrEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("📦 드랍 아이템", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        item.drops.forEach { drop ->
                                            Surface(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(drop, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("✨ 상세 설명", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (isTranslating) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Gemini AI로 한글 번역 중...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(item.description, color = Color.Gray)
                                } else {
                                    Text(translatedDescription ?: item.description)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
