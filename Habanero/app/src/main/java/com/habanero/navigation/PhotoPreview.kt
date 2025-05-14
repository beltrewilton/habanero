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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.habanero.ui.theme.darkgreen
import com.habanero.ui.theme.darkred

@Composable
fun PhotoPreview(
    navController: NavHostController,
    bitmap: Bitmap,
    photoCarList: List<PhotoCar>,
    viewModel: MainViewModel
) {
    val threshold by viewModel.threshold.collectAsState()
    val cropit by viewModel.cropit.collectAsState()
    val hasboxes by viewModel.hasboxes.collectAsState()
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
                        viewModel.setHasboxes(true)
                    },
                    colors = SliderDefaults.colors(
                        activeTrackColor = darkgreen,
                        inactiveTrackColor = Color.LightGray
                    )
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
                cropit,
                hasboxes
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarContent(
    navController: NavHostController,
    bitmap: Bitmap,
    viewModel: MainViewModel,
    threshold: Float,
    cropit: Boolean,
    hasboxes: Boolean
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
                viewModel.setHasboxes(true)
            },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(containerColor = darkgreen),
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

        if (hasboxes) {
            if (cropit) {
                Button(
                    onClick = {
                        viewModel.clearBitmaps()
                        ModelHelper(viewModel).crop(context, threshold, bitmap)
                    },
                    shape = RoundedCornerShape(20),
                    colors = ButtonDefaults.buttonColors(containerColor = darkred),
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
        } else {
            val openAlertDialog = remember { mutableStateOf(true) }
            if (openAlertDialog.value) {
                HDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = { openAlertDialog.value = false })
            }
        }
    }
}

@Composable
fun HDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null)
        },
        title = {
            Text(text = "No leaft found!")
        },
        text = {
            Text(text = "Please try again or adjust the threshold.")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Understood!")
            }
        }
    )
}
