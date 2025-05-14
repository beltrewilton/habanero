package com.habanero.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.habanero.lifecycle.MainViewModel
import com.habanero.ui.theme.darkgreen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(
    title: String? = null,
    titleColor: Color = Color.White,
    viewModel: MainViewModel,
    bottomBar: Boolean = false,
    bottomSheetContent: (@Composable ColumnScope.() -> Unit)? = null,
    bottomBarContent: (@Composable () -> Unit)? = null,
    backgroundColor: Color? = darkgreen,
    backgroundImage: Painter? = null,
    content: @Composable (PaddingValues) -> Unit
) {

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topBarHeight = screenHeightDp * 0.2

    val loading by viewModel.loading.collectAsState()

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)) // Optional dim background
                .zIndex(999f)
                .pointerInput(Unit) {
                    // This captures all touch events to block interaction
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                        }
                    }
                }, // Ensure it is on top
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (backgroundImage != null) {
            Image(
                painter = backgroundImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (backgroundImage == null && backgroundColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }

        val showSheet by viewModel.showSheet.collectAsState()

        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
            confirmValueChange = { true }
        )
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topBarHeight.dp)
                        .background(Color.Transparent)
                        .padding(top = 40.dp), // Top margin inside topBar
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
                    if (title != null) {
                        Text(
                            text = title.uppercase(),
                            fontSize = 21.sp,
                            color = titleColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            floatingActionButton = {
                if (!showSheet && bottomSheetContent != null) {
                    FloatingActionButton(
                        onClick = { viewModel.setShowSheet(true) },
                    ) {
                        Icon(Icons.Filled.Settings, "Floating action button.")
                    }

                }
            },
            bottomBar = {
                if (bottomBar) {
                    BottomAppBar(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        if (bottomBarContent != null) {
                            bottomBarContent()
                        }
                    }
                }
            },
            content = { padding ->

                content(padding)

            }
        )

        if (showSheet && bottomSheetContent != null) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                onDismissRequest = {
                    viewModel.setShowSheet(false)
                },
                sheetState = bottomSheetState,
            ) {
                bottomSheetContent()
            }

            LaunchedEffect(showSheet) {
                if (showSheet) {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                } else {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
                }
            }
        }
    }
}