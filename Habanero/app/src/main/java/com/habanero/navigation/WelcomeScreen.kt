package com.habanero.navigation

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavHostController) {

    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.popBackStack()
        navController.navigate(Home)
    }

    val context = LocalContext.current
    val backgroundImage = remember {
        BitmapPainter(
            BitmapFactory.decodeStream(context.assets.open("background.png")).asImageBitmap()
        )
    }

    Layout(
        title = "",
        viewModel = MainViewModel(),
        bottomBar = true,
        bottomBarContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

            }
        }
//        backgroundImage = backgroundImage,
//        backgroundColor = Color(0xFF5C8518)
    ) { padding ->
        val leaf = remember() {
            BitmapFactory.decodeStream(context.assets.open("leaf.png"))
        }.asImageBitmap()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            FadeInImage(leaf)
        }
    }
}

@Composable
fun FadeInImage(leaf: ImageBitmap) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000)
        )
    }

    Image(
        bitmap = leaf,
        contentDescription = null,
        modifier = Modifier
            .size(300.dp, 300.dp)
            .graphicsLayer(alpha = alpha.value)
    )
}

