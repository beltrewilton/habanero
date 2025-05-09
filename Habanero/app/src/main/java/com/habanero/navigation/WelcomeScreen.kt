package com.habanero.navigation

import HabaneroBackground
import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavHostController) {

    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.popBackStack()
        navController.navigate(Home)
    }

    HabaneroBackground() {
        val context = LocalContext.current
        val leaf = remember() {
            BitmapFactory.decodeStream(context.assets.open("leaf.png"))
        }.asImageBitmap()

        FadeInImage(leaf)
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
            .fillMaxSize()
            .graphicsLayer(alpha = alpha.value)
    )
}

