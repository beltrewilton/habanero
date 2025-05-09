package com.habanero.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.habanero.layout.Layout
import com.habanero.lifecycle.MainViewModel

@Composable
fun SettingScreen() {
    Layout("This is the Setting", viewModel = MainViewModel()) {
        Button(onClick = { }) {
            Text(text = "go to Nothing")
        }
    }
}