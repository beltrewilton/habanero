package com.habanero.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.habanero.core.ModelHelper
import com.habanero.core.update
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel
import com.habanero.lifecycle.PhotoCar
import com.habanero.ui.theme.darkred
import com.habanero.ui.theme.green
import com.habanero.ui.theme.yellow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun PhotoSlideScreen(navController: NavHostController, viewModel: MainViewModel) {
    val photoCarList by viewModel.photoCarList.collectAsState()
    val models = viewModel.models.collectAsState().value
    val selectedModel = viewModel.selectedModel.collectAsState().value
    var selectedIndex = viewModel.selectedIndex.collectAsState().value
    var totalProcessTimeStr = viewModel.totalProcessTimeStr.collectAsState().value
    var totalProcessTime = viewModel.totalProcessTime.collectAsState().value
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Layout(
        title = "Performing inference with model\n$selectedModel",
        titleColor = Color.Black,
        viewModel = viewModel,
        backgroundColor = yellow,
        bottomBar = true,
        bottomSheetContent = {
            Text("Configure Model", modifier = Modifier.padding(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Spacer(modifier = Modifier.height(60.dp))
                Button(
                    onClick = {
                        viewModel.setShowSheet(false)
                        coroutineScope.launch {
                            coroutineScope {
                                models.map { model ->
                                    update(
                                        viewModel = viewModel,
                                        context = context,
                                        localModelName = "models/$model"
                                    )
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(20),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.width(300.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Model update", color = Color.White)
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
            Column {
                LazyRow(
                    modifier = Modifier.padding(top = padding.calculateTopPadding()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photoCarList.size) { index ->
                        Card(
                            modifier = Modifier
                                .width(screenWidthDp.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column() {
                                Box(
                                    modifier = Modifier
                                        .size(screenWidthDp.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    Image(
                                        bitmap = photoCarList[index].bitmap.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Text(
                                        text = "${index + 1} of ${photoCarList.size}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 20.dp, top = 20.dp)
                                            .background(
                                                color = Color.Black.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                if (photoCarList[index].score > 0) {
                                    Column(
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (photoCarList[index].score <= 0.5f)
                                                        Icons.Filled.VerifiedUser
                                                    else
                                                        Icons.Filled.Sick,
                                                    contentDescription = null,
                                                    tint = if (photoCarList[index].score <= 0.5f)
                                                        green
                                                    else
                                                        darkred
                                                )

                                                Spacer(modifier = Modifier.width(8.dp)) // spacing between icon and text

                                                Text(
                                                    text = if (photoCarList[index].score <= 0.5f) "Healthy" else "Disease",
                                                    color = Color.Black,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text(
                                                text = "Score: %.4f".format(photoCarList[index].score),
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 20.dp)
                                            )
                                        }
                                        Text(
                                            text = "Processed in ${photoCarList[index].processTimeStr}",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (totalProcessTime > 0) {
                        Text(text = "Total Process Time: ${totalProcessTimeStr}", fontSize = 20.sp)
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
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(containerColor = green),
            modifier = Modifier.width(150.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.Undo,
                contentDescription = "back",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Go back", color = Color.White)
        }

        Button(
            onClick = {
                ModelHelper(viewModel).inference(selectedModel, context, photoCarList)
            },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier.width(150.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "back",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Inference", color = Color.White)
        }
    }
}