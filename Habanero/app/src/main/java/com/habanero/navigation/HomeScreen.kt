package com.habanero.navigation

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habanero.core.update
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel
import com.habanero.ui.theme.homeSubtitle
import com.habanero.ui.theme.homeTitle
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: MainViewModel, navigateToCamera: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Layout(
        title = homeTitle,
        viewModel = viewModel,
        bottomBar = true,
        bottomBarContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        navigateToCamera()
                    },
                    shape = RoundedCornerShape(20),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Continue", color = Color.White)
                }
            }
        },
        bottomSheetContent = {
            Text("Update YOLO Model", modifier = Modifier.padding(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.setShowSheet(false)
                        coroutineScope.launch {
                            coroutineScope {
                                async {
                                    update(
                                        viewModel = viewModel,
                                        context = context,
                                        localModelName = "models/yolo11n_model_1_float16.tflite"
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
                    Text("Check for update", color = Color.White)
                }
            }
        }
    ) { padding ->
        val context = LocalContext.current
        val leaf = remember() {
            BitmapFactory.decodeStream(context.assets.open("leaf.png"))
        }.asImageBitmap()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = homeSubtitle,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Image(
                bitmap = leaf,
                contentDescription = null,
                modifier = Modifier.size(300.dp, 300.dp)
            )
        }

    }
}