package com.habanero.lifecycle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
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

    fun addPhotoCar(bitmap: Bitmap, score: Float = 0.0f, processTime: Long = 0L) {
        val updated = _photoCarList.value + PhotoCar(bitmap, score, processTime)
        _photoCarList.value = updated
    }

    fun clearBitmaps() {
        _photoCarList.value = emptyList()
    }

    fun updatePhotoCarScore(index: Int, newScore: Float, processTime: Long) {
        val currentList = _photoCarList.value.toMutableList()
        val photoCar = currentList.getOrNull(index) ?: return
        currentList[index] = photoCar.copy(score = newScore, processTime = processTime)
        _photoCarList.value = currentList
    }

    private val _totalProcessTime = MutableStateFlow(0L)
    val totalProcessTime = _totalProcessTime.asStateFlow()

    private val _totalProcessTimeStr = MutableStateFlow("")
    val totalProcessTimeStr = _totalProcessTimeStr.asStateFlow()

    fun setTotalProcessTime(t: Long) {
        _totalProcessTime.value = t
        val seconds = _totalProcessTime.value / 1000
        val millis = _totalProcessTime.value % 1000
        _totalProcessTimeStr.value  = "${seconds}s ${millis}ms"
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

    private val _loading = MutableStateFlow(value = false)
    val loading = _loading.asStateFlow()

    fun setLoading(b: Boolean) {
        _loading.value = b
    }

    private val _showDialog = MutableStateFlow(value = false)
    val showDialog = _showDialog.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle = _dialogTitle.asStateFlow()

    private val _dialogSubTitle = MutableStateFlow("")
    val dialogSubTitle = _dialogSubTitle.asStateFlow()

    private val _dialogIcon = MutableStateFlow(Icons.Default.Warning)
    val dialogIcon = _dialogIcon.asStateFlow()

    fun showDialog(b: Boolean, t: String = "", s: String = "", icon: ImageVector = Icons.Default.Warning) {
        _showDialog.value = b
        _dialogTitle.value = t
        _dialogSubTitle.value = s
        _dialogIcon.value = icon
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

    private val _hasboxes = MutableStateFlow(value = true)
    val hasboxes = _hasboxes.asStateFlow()

    fun setHasboxes(b: Boolean) {
        _hasboxes.value = b
    }
}

data class PhotoCar(
    val bitmap: Bitmap,
    val score: Float = 0.0f,
    val processTime: Long = 0L,
) {
    val processTimeStr: String
        get() {
            val seconds = processTime / 1000
            val millis = processTime % 1000
            return "${seconds}s ${millis}ms"
        }
}