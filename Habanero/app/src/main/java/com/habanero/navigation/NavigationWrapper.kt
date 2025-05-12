package com.habanero.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habanero.lifecycle.MainViewModel

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val viewModel = viewModel<MainViewModel>()

    NavHost(navController = navController, startDestination = Welcome) {
        composable<Welcome> {
            WelcomeScreen(navController)
        }

        composable<Home> {
            HomeScreen(viewModel) { navController.navigate(Camera) }
        }

        composable<Camera> {
            CameraScreen(navController, viewModel)
        }

        composable<PhotoSlide> {
            PhotoSlideScreen(navController, viewModel)
        }

        composable<Setting> {
            SettingScreen()
        }
    }
}