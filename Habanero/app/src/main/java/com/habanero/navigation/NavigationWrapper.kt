package com.habanero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Camera) {
        composable<Welcome> {
            WelcomeScreen(navController)
        }

        composable<Home> {
            HomeScreen { navController.navigate(Setting) }
        }

        composable<Camera> {
            CameraScreen()
        }

        composable<Setting> {
            SettingScreen()
        }
    }
}