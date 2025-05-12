package com.habanero.layout

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habanero.lifecycle.MainViewModel
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
    backgroundColor: Color? = Color(0xFF5C8518),
    backgroundImage: Painter? = null,
    content: @Composable (PaddingValues) -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image if provided
        if (backgroundImage != null) {
            Image(
                painter = backgroundImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Background color if provided and image is not used
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
                if (title != null) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = title,
                                fontSize = 22.sp,
                                color = titleColor
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
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