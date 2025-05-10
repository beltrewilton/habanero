package com.habanero.navigation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.habanero.core.ModelHelper
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel
import com.habanero.lifecycle.PhotoCar

@Composable
fun PhotoPreview(
    navController: NavHostController,
    bitmap: Bitmap,
    photoCarList: List<PhotoCar>,
    viewModel: MainViewModel
) {
    val threshold by viewModel.threshold.collectAsState()
    val cropit by viewModel.cropit.collectAsState()
    val context = LocalContext.current

    Layout(
        title = "Photo Preview",
        viewModel = viewModel,
        bottomBar = true,
        bottomSheetContent = {
            Text("Configure threshold", modifier = Modifier.padding(16.dp))

            Column {
                Slider(
                    value = threshold,
                    onValueChange = {
                        viewModel.setThreshold(it, context)
                    },
                    onValueChangeFinished = {
                        viewModel.backToFreshPhoto(context)
                        viewModel.setCropit(true)
                    }
                )
                Text(text = threshold.toString())
            }
        },
        bottomBarContent = {
            BottomBarContent(
                navController,
                bitmap,
                viewModel,
                threshold,
                cropit
            )
        }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }


    /**    Box(
    modifier = Modifier
    .fillMaxSize()
    .padding(20.dp)
    ) {
    Text(
    text = "PhotoPreview",
    fontSize = 30.sp,
    color = Color(0xFF225B22),
    modifier = Modifier.padding(vertical = 20.dp)
    )
    val shape = listOf(bitmap.width,  bitmap.height).joinToString(", ")
    Log.d("BITMAP", "PhotoPreview shape -> $shape")

    Image(
    bitmap = bitmap.asImageBitmap(),
    contentDescription = null,
    modifier = Modifier.fillMaxSize()
    )

    Button(onClick = {
    viewModel.resetBitmap(bitmap)
    }) { Text( text = "Re-take photo") }
    }
     */
}

@Composable
fun BottomBarContent(
    navController: NavHostController,
    bitmap: Bitmap,
    viewModel: MainViewModel,
    threshold: Float,
    cropit: Boolean
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                viewModel.resetBitmap(bitmap)
                viewModel.clearBitmaps()
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00963D))
        ) {
            Text("Retake", color = Color.White)
        }

        if (cropit) {
            Button(
                onClick = {
                    viewModel.clearBitmaps()
                    ModelHelper(viewModel).crop(context, threshold, bitmap)
                    viewModel.setCropit(false)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Crop it", color = Color.Black)
            }
        } else {
            Button(
                onClick = { navController.navigate(PhotoSlide) },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Continue", color = Color.Black)
            }
        }
    }
}
