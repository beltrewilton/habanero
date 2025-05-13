package com.habanero.navigation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        viewModel = viewModel,
        bottomBar = true,
        bottomSheetContent = {
            Text("Configure threshold", modifier = Modifier.padding(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = threshold,
                    onValueChange = {
                        viewModel.setThreshold(it)
                    },
                    onValueChangeFinished = {
                        viewModel.backToFreshPhoto(context)
                        viewModel.setCropit(true)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = threshold.toString(), fontSize = 25.sp)
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
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
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
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00963D)),
            modifier = Modifier.width(150.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retake",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retake", color = Color.White)
        }

        if (cropit) {
            Button(
                onClick = {
                    viewModel.clearBitmaps()
                    ModelHelper(viewModel).crop(context, threshold, bitmap)
                    viewModel.setCropit(false)
                },
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF730606)),
                modifier = Modifier.width(150.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Crop,
                    contentDescription = "Crop",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crop it", color = Color.White)
            }
        } else {
            Button(
                onClick = { navController.navigate(PhotoSlide) },
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier.width(150.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "continue",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue", color = Color.White)
            }
        }
    }
}
