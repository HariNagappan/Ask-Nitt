package com.example.asknitt.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.asknitt.R
import com.example.asknitt.data.model.AllScreensNamesItem
import com.example.asknitt.data.routes.MainScreenRoutes

@Composable
fun CustomBottomNavigationBar(navController: NavHostController) {
    val entries = GetBottomBarEntries()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.hierarchy

    if (currentRoute?.any { it.route == MainScreenRoutes.MAIN.name } == true) {
        NavigationBar(
            containerColor = Color.Black,
            tonalElevation = 10.dp,
            modifier = Modifier.height(80.dp)
        ) {
            entries.forEach { entry ->
                val isSelected = currentRoute.any { it.route == entry.route } == true
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navController.navigate(entry.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = entry.icon,
                                contentDescription = null,
                                tint = colorResource(if (isSelected) R.color.electric_green else R.color.white),
                            )
                            AnimatedVisibility(visible = isSelected) {
                                Text(
                                    text = entry.label.uppercase(),
                                    color = colorResource(R.color.electric_green),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
    }
}
fun GetBottomBarEntries():List<AllScreensNamesItem>{
    return listOf(
        AllScreensNamesItem(
            route = MainScreenRoutes.HOME.name,
            label = "Home",
            icon = Icons.Default.Home
        ),
        AllScreensNamesItem(
            route = MainScreenRoutes.MY_DOUBTS.name,
            label = "My Doubts",
            icon = Icons.AutoMirrored.Filled.Assignment
        ),
        AllScreensNamesItem(
            route = MainScreenRoutes.SEARCH_STUFF.name,
            label = "Search",
            icon = Icons.Default.Search
        ),
        AllScreensNamesItem(
            route = MainScreenRoutes.EXPLORE_USERS_STUFF.name,
            label = "Explore",
            icon = Icons.Default.People
        ),
        AllScreensNamesItem(
            route = MainScreenRoutes.SETTINGS.name,
            label = "Settings",
            icon = Icons.Default.Settings
        )

    )
}
