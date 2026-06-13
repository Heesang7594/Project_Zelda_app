package com.example.thelegendofzelda.presentation.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thelegendofzelda.R
import com.example.thelegendofzelda.data.model.Shrine
import com.example.thelegendofzelda.ui.theme.ZeldaGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val shrines by viewModel.filteredShrines.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += offsetChange
    }

    var selectedShrine by remember { mutableStateOf<Shrine?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("하이랄 지도", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZeldaGreen)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1E2124))
        ) {
            // Map Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { selectedShrine = null }
                        )
                    }
                    .transformable(state = state)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                // Background Map Image
                Image(
                    painter = painterResource(id = R.drawable.hyrule_map),
                    contentDescription = "Hyrule Map",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Shrine Markers
                shrines.forEach { shrine ->
                    Box(
                        modifier = Modifier
                            .absoluteOffset(x = shrine.x.dp, y = shrine.y.dp)
                            .size(36.dp)
                            .clickable { selectedShrine = shrine },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = shrine.nameKo,
                            tint = if (shrine.isCleared) Color(0xFF4CAF50) else Color(0xFF2196F3),
                            modifier = Modifier.size(36.dp)
                        )
                        if (shrine.isCleared) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Cleared",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp).align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            // Filter Menu
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .width(200.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("필터", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
                    FilterOption("모든 사당", currentFilter == ShrineFilter.ALL) {
                        viewModel.setFilter(ShrineFilter.ALL)
                    }
                    FilterOption("클리어한 사당", currentFilter == ShrineFilter.CLEARED) {
                        viewModel.setFilter(ShrineFilter.CLEARED)
                    }
                    FilterOption("클리어하지 않은 사당", currentFilter == ShrineFilter.UNCLEARED) {
                        viewModel.setFilter(ShrineFilter.UNCLEARED)
                    }
                }
            }

            // Shrine Info Popup
            selectedShrine?.let { shrine ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(shrine.nameKo, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(shrine.region, fontSize = 14.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("클리어", fontSize = 14.sp)
                                Checkbox(
                                    checked = shrine.isCleared,
                                    onCheckedChange = { viewModel.toggleShrineCleared(shrine.id) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(shrine.tip, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected) ZeldaGreen.copy(alpha = 0.2f) else Color.Transparent)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = if (isSelected) ZeldaGreen else MaterialTheme.colorScheme.onSurface)
    }
}
