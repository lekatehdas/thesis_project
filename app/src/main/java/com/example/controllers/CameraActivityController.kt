package com.example.controllers

import android.app.Activity
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageProxy
import com.example.data_gatherers.CameraDataGatherer
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KSuspendFunction0

class CameraActivityController(
    private val dataHolder: DataHolder<ByteArray>,
    private val sources: List<String>,
    private val activity: Activity,
    private val binding: ActivityCameraBinding,
    private val updateUi: KSuspendFunction0<Unit>,
    private val resetUi: KSuspendFunction0<Unit>
) {

    private val cameraGatherer = CameraDataGatherer(activity, binding, ::onImageData)
    private val camera = sources[0]

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun start() {
        cameraGatherer.startCamera()
    }

    fun stop() {
        cameraGatherer.stopCamera()
    }

    private fun onImageData(imageData: ImageProxy) {
        val results = getBits(imageData)
        for (result in results)
            dataHolder.addElementToList(camera, result)

        if (dataHolder.getListSize(camera) >= Constants.DESIRED_LENGTH_CAMERA) {
            saveData(dataHolder.getListByName(camera))
            dataHolder.clearAllLists()
            activity.runOnUiThread { runBlocking {  resetUi() } }
        }

        activity.runOnUiThread { runBlocking {  updateUi() } }
    }

    private fun getBits(imageData: ImageProxy): List<ByteArray> {
        val bitmap = imageData.toBitmap()
        val width = bitmap.width
        val height = bitmap.height

        val gridSize = kotlin.math.ceil(kotlin.math.sqrt(1000.0)).toInt()
        val cellWidth = width / gridSize
        val cellHeight = height / gridSize

        val result = mutableListOf<ByteArray>()

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val x = i * cellWidth + cellWidth / 2
                val y = j * cellHeight + cellHeight / 2

                if (x < width && y < height) {
                    val pixel = bitmap.getPixel(x, y)
                    val r = pixel shr 16 and 0xFF
                    val g = pixel shr 8 and 0xFF
                    val b = pixel and 0xFF

                    val leastSignificantBytes = byteArrayOf(
                        r.toByte(),
                        g.toByte(),
                        b.toByte()
                    )

                    result.add(leastSignificantBytes)
                }
            }
        }

        return result
    }

    private fun saveData(events: List<ByteArray>) {
        ioScope.launch {
            try {
                val fileName = "${System.currentTimeMillis()}.csv"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.Files.FileColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/Thesis/Camera/")
                }

                val uri = activity.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let { fileUri ->
                    activity.contentResolver.openOutputStream(fileUri).use { outputStream ->
                        outputStream?.bufferedWriter().use { writer ->
                            events.forEach { byteArray ->
                                val bytesAsBinaryStrings = byteArray.joinToString(
                                    separator = ",",
                                    transform = { "`" + String.format("%8s", Integer.toBinaryString(it.toInt() and 0xFF)).replace(' ', '0') })
                                writer?.write("$bytesAsBinaryStrings\n")
                            }
                            writer?.flush()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
