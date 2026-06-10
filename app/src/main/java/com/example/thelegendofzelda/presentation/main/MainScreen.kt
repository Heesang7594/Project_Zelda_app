package com.example.thelegendofzelda.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thelegendofzelda.navigation.Screen
import com.example.thelegendofzelda.ui.theme.*

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "하이랄 도감 (왕국의 눈물)",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ZeldaGreen,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "젤다의 전설 왕국의 눈물 아이템 정보",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCard(
                title = "생물",
                subtitle = "동식물, 버섯류",
                color = CreatureGreen,
                modifier = Modifier.weight(1f)
            ) {
                navController.navigate(Screen.CategoryList.createRoute("creatures"))
            }
            CategoryCard(
                title = "몬스터",
                subtitle = "마물, 골렘 등",
                color = MonsterRed,
                modifier = Modifier.weight(1f)
            ) {
                navController.navigate(Screen.CategoryList.createRoute("monsters"))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCard(
                title = "장비",
                subtitle = "무기, 활, 조나우 기어",
                color = EquipmentBlue,
                modifier = Modifier.weight(1f)
            ) {
                navController.navigate(Screen.CategoryList.createRoute("equipment"))
            }
            CategoryCard(
                title = "소재",
                subtitle = "요리 재료, 광석, 결정",
                color = MaterialOrange,
                modifier = Modifier.weight(1f)
            ) {
                navController.navigate(Screen.CategoryList.createRoute("materials"))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Guide Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💡 사용 가이드", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text("• 카테고리를 선택하여 아이템 목록 확인\n• AI 검색으로 상황별 추천 받기\n• 공략 탭에서 보스 전략 영상 시청", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}
