package com.jakubn.codequizapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jakubn.codequizapp.navigation.BottomNavItem
import com.jakubn.codequizapp.navigation.Screen

@Composable
fun MainScreen(
    navController: NavHostController,
    content: @Composable (modifier: Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val routes = listOf(
        BottomNavItem(
            Screen.Home.route,
            ImageVector.vectorResource(R.drawable.ic_nav_home),
            "Home"
        ),
        BottomNavItem(
            Screen.Leaderboard.route,
            ImageVector.vectorResource(R.drawable.ic_nav_leaderboard),
            "Leaderboard"
        ),
        BottomNavItem(
            Screen.MyProfile.route,
            ImageVector.vectorResource(R.drawable.ic_nav_settings),
            "Settings"
        )
    )
    Scaffold(
        bottomBar = {
            if (currentRoute == null) return@Scaffold

            BottomNavigation(backgroundColor = Color(0xF2000226)) {
                routes.forEach { screen ->
                    BottomNavigationItem(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        icon = {
                            Icon(
                                modifier = Modifier.padding(vertical = 10.dp),
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                tint = Color(0xffA3FF0D)
                            )
                        },
                        selected = currentRoute == screen.route,
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
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}