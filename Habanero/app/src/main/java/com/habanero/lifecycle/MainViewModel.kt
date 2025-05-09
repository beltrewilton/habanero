package com.habanero.lifecycle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream


class MainViewModel: ViewModel() {
    private val _bitmap = MutableStateFlow ( value = createBitmap(1, 1) )
    val bitmap = _bitmap.asStateFlow()
//    private val _xbitmap = MutableStateFlow ( value = createBitmap(1, 1) )
//    val xbitmap = _xbitmap.asStateFlow()

    fun onBoxedPhoto(bitmap: Bitmap) {
//        val shape = listOf(bitmap.width,  bitmap.height).joinToString(", ")
//        Log.d("BITMAP", "MainViewModel shape -> $shape")
        _bitmap.value = bitmap
//        println("_bitmap hash: ${System.identityHashCode(_bitmap.value)}")
    }

    fun onTakePhoto(bitmap: Bitmap, context: Context) {
//        val shape = listOf(bitmap.width,  bitmap.height).joinToString(", ")
//        Log.d("BITMAP", "MainViewModel shape -> $shape")

//        val immutableCopy = bitmap.copy(bitmap.config!!,  false)

//        _xbitmap.value =  bitmap.copy(Bitmap.Config.ARGB_8888, false)

//        val immutableBackup = bitmap.copy(Bitmap.Config.ARGB_8888, false)
//        val mutableForEdit = immutableBackup.copy(Bitmap.Config.ARGB_8888, true)
//
//        _xbitmap.value = immutableBackup
//        _bitmap.value = mutableForEdit

//        Log.d("backup Restore_DrawBoxes", "Restored bitmap hash: ${xbitmap.hashCode()}")

        _bitmap.value = bitmap

        val file = File(context.filesDir, "fresh-picture.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    fun resetBitmap(bitmap: Bitmap) {
        _bitmap.value = createBitmap(1, 1)
    }

    fun backToFreshPhoto(context: Context) {
        val file = File(context.filesDir, "fresh-picture.png")
        val bm = BitmapFactory.decodeStream(file.inputStream())
        _bitmap.value = bm
    }

//    fun restore() {
//        _bitmap.value = xbitmap.value.copy(Bitmap.Config.ARGB_8888, false)
//        Log.d("restore Restore_DrawBoxes", "Restored bitmap hash: ${_xbitmap.hashCode()}")
//    }


//    fun resetxBitmap() {
//        _xbitmap.value = createBitmap(1, 1)
//    }

    private val _showSheet = MutableStateFlow (value = false)
    val showSheet = _showSheet.asStateFlow()

    fun setShowSheet(b: Boolean) {
        _showSheet.value = b
    }

    private val _threshold = MutableStateFlow (0.6f)
    val threshold = _threshold.asStateFlow()

    fun setThreshold(t: Float, context: Context) {
        _threshold.value = t
    }
}