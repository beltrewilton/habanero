package com.habanero.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import com.habanero.lifecycle.MainViewModel
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.roundToInt

class ModelHelper(private val viewModel: MainViewModel) {

    //    @Composable
    fun crop(context: Context, threshold: Float, bitmap: Bitmap, bitmapList: List<Bitmap>) {
//        var imageBitmaps by remember { mutableStateOf(listOf<Bitmap>()) }
//        viewModel.restore()
//        viewModel.resetBitmap(bitmap)
        val resized = Bitmap.createScaledBitmap(bitmap, 640, 640, true) // BitMap!
        val input = convertBitmapToByteBuffer(resized, 640)

        val output = Array(1) { Array(300) { FloatArray(6) } }

        val interpreter = getInterpreter(context, "models/yolo11n_model_1_float16.tflite")
        interpreter.run(input, output)

        val boxes = mutableListOf<Rect>()


        for ((i, out) in output[0].withIndex()) {
            if (out[4] >= threshold) {
                var x1 = (out[0] * bitmap.width).roundToInt()
                var y1 = (out[1] * bitmap.height).roundToInt()
                var x2 = (out[2] * bitmap.width).roundToInt()
                var y2 = (out[3] * bitmap.height).roundToInt()
                val score = "%.2f".format(out[4])

                x1 = minOf(maxOf(0, x1), bitmap.width)
                y1 = minOf(maxOf(0, y1), bitmap.height)
                x2 = minOf(maxOf(0, x2), bitmap.width)
                y2 = minOf(maxOf(0, y2), bitmap.height)


                val cropped = Bitmap.createBitmap(bitmap, x1, y1, x2 - x1, y2 - y1)

                viewModel.addBitmap(cropped)

                val file = File(context.filesDir, "$i-cropped.png")
                val outputStream = FileOutputStream(file)
                cropped.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                println("Threshold: $threshold, Score: $score, Box($x1, $y1, $x2, $y2)")

                boxes.add(Rect(x1, y1, x2, y2))
            }
        }

        val boxedBitmap = drawBoxes(bitmap, boxes)
        viewModel.onBoxedPhoto(boxedBitmap)
    }

    fun inference(context: Context, bitmapList: List<Bitmap>) {
        val modelName = "vgg16_model_2.tflite"
//        val modelName = "mnet_model_2s.tflite"
        val interpreter = getInterpreter(context, "models/$modelName")

        for ((index, bitmap) in bitmapList.withIndex()) {
            val resized = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
            val input = convertBitmapToByteBuffer(resized, 256)
            val output = Array(1) { FloatArray(1) }
            interpreter.run(input, output)
            val pick = output[0].joinToString(", ") { String.format("%.4f", it) }
            Log.d("MODEL RESULT $index", "$index: $pick")
        }
    }

    private fun getInterpreter(context: Context, path: String): Interpreter {
        val interpreter = Interpreter(loadModelFile(context, path))
        val inputCount = interpreter.inputTensorCount
        val outputCount = interpreter.outputTensorCount
        val inputShape = interpreter.getInputTensor(0).shape()
        val inputType = interpreter.getInputTensor(0).dataType()
        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputType = interpreter.getOutputTensor(0).dataType()

        Log.d(
            path,
            "Inputs: $inputCount, shape: ${inputShape.contentToString()}, type: $inputType"
        )
        Log.d(
            path,
            "Outputs: $outputCount, shape: ${outputShape.contentToString()}, type: $outputType"
        )

        return interpreter
    }

    private fun loadModelFile(context: Context, filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    private fun convertBitmapToByteBuffer(bitmap: Bitmap, size: Int): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(1 * size * size * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(size * size)
        bitmap.getPixels(pixels, 0, size, 0, 0, size, size)

        for (pixel in pixels) {
            inputBuffer.putFloat(Color.red(pixel) / 255f)
            inputBuffer.putFloat(Color.green(pixel) / 255f)
            inputBuffer.putFloat(Color.blue(pixel) / 255f)
        }

        inputBuffer.rewind() // resets the buffer's position to 0
        return inputBuffer
    }

    private fun drawBoxes(bitmap: Bitmap, boxes: List<Rect>): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val paint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        for (box in boxes) { canvas.drawRect(box, paint) }

        return mutableBitmap
    }

}