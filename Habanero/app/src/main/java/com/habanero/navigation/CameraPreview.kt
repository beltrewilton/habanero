package com.habanero.navigation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel
import java.util.concurrent.Executors


@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    viewModel: MainViewModel,
    current: Context,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    Layout(
        viewModel = MainViewModel(),
        bottomBar = false,
        bottomBarContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

            }
        },
        backgroundColor = Color(0xFFE8E854)
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    PreviewView(it).apply {
                        this.controller = controller
                        controller.bindToLifecycle(lifecycleOwner)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(80.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller,
                            current = current,
                            onPhotoTaken = viewModel::onTakePhoto
                        )
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take the photo",
                        modifier = Modifier.size(32.dp)
                    )
                }

            }
        }
    }
}

private fun takePhoto(
    controller: LifecycleCameraController,
    current: Context,
    onPhotoTaken: (Bitmap, Context) -> Unit
) {
    val cameraExecutor = Executors.newSingleThreadExecutor()
    controller.takePicture(
        cameraExecutor,
        object : OnImageCapturedCallback() {
            override fun onCaptureStarted() {
                super.onCaptureStarted()
            }

            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }

                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                val shape = listOf(rotatedBitmap.width, rotatedBitmap.height).joinToString(", ")
                onPhotoTaken(rotatedBitmap, current)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }

            override fun onCaptureProcessProgressed(progress: Int) {
                super.onCaptureProcessProgressed(progress)
            }

            override fun onPostviewBitmapAvailable(bitmap: Bitmap) {
                super.onPostviewBitmapAvailable(bitmap)
                val shape = listOf(bitmap.width, bitmap.height).joinToString(", ")
            }
        }
    )
}