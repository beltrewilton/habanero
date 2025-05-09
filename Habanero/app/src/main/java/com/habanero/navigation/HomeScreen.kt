package com.habanero.navigation

import HabaneroBackground
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel

@Composable
fun HomeScreen(navigateToSetting: () -> Unit) {
    Layout("HABANERO DISEASE", viewModel = MainViewModel()) {

        HabaneroBackground() {
            val context = LocalContext.current
            val leaf = remember() {
                BitmapFactory.decodeStream(context.assets.open("leaf.png"))
            }.asImageBitmap()

            Image(
                bitmap = leaf,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

//        Button(onClick = { navigateToSetting() }) {
//            Text(text = "go to Setting")
//        }
    }
}