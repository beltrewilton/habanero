package com.habanero.core

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Rocket
import com.habanero.lifecycle.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

suspend fun update(viewModel: MainViewModel, context: Context, localModelName: String): Boolean =
    withContext(Dispatchers.IO) {
        viewModel.setLoading(true)
        val updated = checkAndUpdateModel(
            context,
            modelUrl = "https://github.com/beltrewilton/habanero/raw/refs/heads/main/Habanero/app/src/main/assets/${localModelName}",
            localModelName = localModelName
        )
        viewModel.setLoading(false)

        if (updated) {
            Log.d("UPDATED", "✔︎ MODEL $localModelName UPDATED")
            viewModel.showDialog(true, t = "Model updated!", s = "The model $localModelName was updated successfully", icon = Icons.Default.Rocket)
        } else {
            Log.d("UPDATED", "${localModelName}  NOT NEED TO BE UPDATED ")
            viewModel.showDialog(true, t = "Model is up to date", s = "The model $localModelName is okay.", icon = Icons.Default.Check)
        }

        updated
    }


suspend fun checkAndUpdateModel(
    context: Context,
    modelUrl: String,
    localModelName: String
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val localFile = File(context.filesDir, localModelName)

            // Connect to GitHub and check for model
            val url = URL(modelUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != 200) {
                return@withContext false
            }

            val remoteBytes = connection.inputStream.readBytes()
            val remoteChecksum = remoteBytes.contentHashCode()

            if (localFile.exists()) {
                val localChecksum = localFile.readBytes().contentHashCode()
                if (remoteChecksum == localChecksum) {
                    return@withContext false // No update needed
                }
            }

            // Save new model
            FileOutputStream(localFile).use { it.write(remoteBytes) }
            true // Model updated
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

fun ensureModelFileExists(context: Context, filename: String): File {
    val modelsDir = File(context.filesDir, "models")
    if (!modelsDir.exists()) {
        if (!modelsDir.mkdirs()) {
            throw RuntimeException("Failed to create models directory at ${modelsDir.absolutePath}")
        }
    }

    val targetFile = File(context.filesDir, filename)

    if (!targetFile.exists()) {
        try {
            context.assets.open(filename).use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to copy $filename from assets to internal storage")
        }
    }

    return targetFile
}
