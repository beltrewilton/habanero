package com.habanero.navigation

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel, navigateToCamera: () -> Unit) {
    val context = LocalContext.current
    val backgroundImage = remember {
        BitmapPainter(
            BitmapFactory.decodeStream(context.assets.open("background.png")).asImageBitmap()
        )
    }

    Layout(
        title = "HABANERO DISEASE",
        viewModel = viewModel,
        bottomBar = true,
//        backgroundImage = backgroundImage,
//        backgroundColor = Color.Yellow,
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
        }) { padding ->
//        HabaneroBackground() {
//
//        }
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
            Image(
                bitmap = leaf,
                contentDescription = null,
                modifier = Modifier.size(300.dp, 300.dp)
            )
        }

    }
}