package com.example.thelegendofzelda.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thelegendofzelda.presentation.ai_search.AiSearchScreen
import com.example.thelegendofzelda.presentation.compendium.CategoryListScreen
import com.example.thelegendofzelda.presentation.compendium.ItemDetailScreen
import com.example.thelegendofzelda.presentation.guide.VideoListScreen
import com.example.thelegendofzelda.presentation.main.MainScreen

sealed class Screen(val route: String, val title: String? = null, val icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    object Main : Screen("main", "도감", Icons.Filled.List)
    object AiSearch : Screen("ai_search", "AI 검색", Icons.Filled.Search)
    object GuideVideo : Screen("guide_video", "공략", Icons.Filled.PlayArrow)
    
    object CategoryList : Screen("category/{category}") {
        fun createRoute(category: String) = "category/$category"
    }
    object ItemDetail : Screen("item/{category}/{id}") {
        fun createRoute(category: String, id: Int) = "item/$category/$id"
    }
}

val bottomNavItems = listOf(
    Screen.Main,
    Screen.AiSearch,
    Screen.GuideVideo
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Show bottom bar only on top-level destinations
            val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title!!) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Main.route) {
                MainScreen(navController)
            }
            composable(Screen.CategoryList.route) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                CategoryListScreen(navController, category)
            }
            composable(
                route = Screen.ItemDetail.route,
                arguments = listOf(
                    androidx.navigation.navArgument("category") { type = androidx.navigation.NavType.StringType },
                    androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.IntType }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                ItemDetailScreen(navController, category, id)
            }
            composable(Screen.AiSearch.route) {
                AiSearchScreen()
            }
            composable(Screen.GuideVideo.route) {
                VideoListScreen()
            }
        }
    }
}
