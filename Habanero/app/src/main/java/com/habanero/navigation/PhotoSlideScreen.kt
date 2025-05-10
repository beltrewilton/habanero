package com.habanero.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.habanero.core.ModelHelper
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel
import com.habanero.lifecycle.PhotoCar

@Composable
fun PhotoSlideScreen(navController: NavHostController, viewModel: MainViewModel) {
    val photoCarList by viewModel.photoCarList.collectAsState()
    val models = viewModel.models.collectAsState().value
    val selectedModel = viewModel.selectedModel.collectAsState().value
    var selectedIndex = viewModel.selectedIndex.collectAsState().value

    Layout(
        title = "Inference with $selectedModel",
        viewModel = viewModel,
        bottomBar = true,
        bottomSheetContent = {
            Text("Configure Model", modifier = Modifier.padding(16.dp))

            SingleChoiceSegmentedButtonRow {
                models.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = models.size
                        ),
                        onClick = {
                            selectedIndex = index
                            viewModel.setModelByIndex(selectedIndex)
                        },
                        selected = index == selectedIndex,
                        label = { Text(label) }
                    )
                }
            }
        },
        bottomBarContent = {
            SlideBottomBarContent(
                navController = navController,
                viewModel = viewModel,
                photoCarList = photoCarList,
                selectedModel = selectedModel
            )
        }) { padding ->
        if (photoCarList.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(padding),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photoCarList.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(380.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            bitmap = photoCarList[index].bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Text(
                            text = "${index + 1} of ${photoCarList.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        if (photoCarList[index].score > 0) {
                            Text(
                                text = "Score: ${photoCarList[index].score}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SlideBottomBarContent(
    navController: NavHostController,
    viewModel: MainViewModel,
    photoCarList: List<PhotoCar>,
    selectedModel: String
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
                navController.navigate(Camera)
            },
            shape = RoundedCornerShape(10),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00963D))
        ) {
            Text("Go back", color = Color.White)
        }

        Button(
            onClick = { ModelHelper(viewModel).inference(selectedModel, context, photoCarList) },
            shape = RoundedCornerShape(10),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Inference", color = Color.White)
        }
    }
}