package com.habanero.navigation

import android.util.Log
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.habanero.lifecycle.MainViewModel

@Composable
fun CameraScreen(navController: NavHostController, viewModel: MainViewModel) {
    val current = LocalContext.current
    val bitmap by viewModel.bitmap.collectAsState()
    val photoCarList by viewModel.photoCarList.collectAsState()

    val controller = remember {
        LifecycleCameraController(current).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    val shape = listOf(bitmap.width, bitmap.height).joinToString(", ")
    Log.d("BITMAP", "CameraScreen shape -> $shape")

    if (bitmap.width == 1 && bitmap.height == 1) {
        CameraPreview(controller = controller, viewModel = viewModel, current = current)
    } else {
        PhotoPreview(navController, bitmap, photoCarList, viewModel)
    }

}
