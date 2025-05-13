package com.habanero.lifecycle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream


class MainViewModel : ViewModel() {
    private val _bitmap = MutableStateFlow(value = createBitmap(1, 1))
    val bitmap: StateFlow<Bitmap> = _bitmap.asStateFlow()

    fun onBoxedPhoto(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun onTakePhoto(bitmap: Bitmap, context: Context) {
        _bitmap.value = bitmap

        val file = File(context.filesDir, "fresh-picture.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        setCropit(true)
    }

    fun resetBitmap(bitmap: Bitmap) {
        _bitmap.value = createBitmap(1, 1)
    }

    fun backToFreshPhoto(context: Context) {
        val file = File(context.filesDir, "fresh-picture.png")
        val bm = BitmapFactory.decodeStream(file.inputStream())
        _bitmap.value = bm
    }


    private val _photoCarList = MutableStateFlow<List<PhotoCar>>(emptyList())
    val photoCarList: StateFlow<List<PhotoCar>> = _photoCarList.asStateFlow()

    fun addPhotoCar(bitmap: Bitmap, score: Float = 0.0f) {
        val updated = _photoCarList.value + PhotoCar(bitmap, score)
        _photoCarList.value = updated
    }

    fun clearBitmaps() {
        _photoCarList.value = emptyList()
    }

    fun updatePhotoCarScore(index: Int, newScore: Float) {
        val currentList = _photoCarList.value.toMutableList()
        val photoCar = currentList.getOrNull(index) ?: return
        currentList[index] = photoCar.copy(score = newScore)
        _photoCarList.value = currentList
    }


    private val _models =
        MutableStateFlow<List<String>>(listOf("vgg16_model_2.tflite", "mnet_model_2s.tflite"))
    val models: StateFlow<List<String>> = _models.asStateFlow()

    private val _selectedIndex = MutableStateFlow<Int>(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    private val _selectedModel = MutableStateFlow<String>(_models.value.get(_selectedIndex.value))
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    fun setModelByIndex(index: Int) {
        val selected = _models.value.get(index)
        _selectedModel.value = selected

        _selectedIndex.value = index
    }

    private val _showSheet = MutableStateFlow(value = false)
    val showSheet = _showSheet.asStateFlow()

    fun setShowSheet(b: Boolean) {
        _showSheet.value = b
    }

    private val _threshold = MutableStateFlow(0.6f)
    val threshold = _threshold.asStateFlow()

    fun setThreshold(t: Float) {
        _threshold.value = t
    }

    private val _cropit = MutableStateFlow(value = true)
    val cropit = _cropit.asStateFlow()

    fun setCropit(b: Boolean) {
        _cropit.value = b
    }
}

data class PhotoCar(
    val bitmap: Bitmap,
    val score: Float = 0.0f
)