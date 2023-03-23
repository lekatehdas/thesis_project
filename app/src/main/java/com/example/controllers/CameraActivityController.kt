package com.example.controllers

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.camera.core.ImageProxy
import com.example.data_gatherers.CameraDataGatherer
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.reflect.KSuspendFunction0

class CameraActivityController(
    private val dataHolder: DataHolder<Map<String, String>>,
    private val sources: List<String>,
    private val activity: Activity,
    private val binding: ActivityCameraBinding,
    private val updateUi: KSuspendFunction0<Unit>,
    private val resetUi: KSuspendFunction0<Unit>
) {

    private val cameraGatherer = CameraDataGatherer(activity, binding, ::onImageData)
    private val camera = sources[0]

    fun start() {
        cameraGatherer.startCamera()
    }

    fun stop() {
        cameraGatherer.stopCamera()
    }

    private fun onImageData(imageData: ImageProxy) {
        val eventData = mapOf(
            "yData" to Base64.encodeToString(imageData.planes[0].buffer.toByteArray(), Base64.DEFAULT),
            "uData" to Base64.encodeToString(imageData.planes[1].buffer.toByteArray(), Base64.DEFAULT),
            "vData" to Base64.encodeToString(imageData.planes[2].buffer.toByteArray(), Base64.DEFAULT)
        )

        dataHolder.addElementToList(camera, eventData)

        if (dataHolder.getListSize(camera) == Constants.DESIRED_LENGTH_CAMERA) {
            writeEventsToFile(dataHolder.getListByName(camera))
            dataHolder.clearAllLists()
            activity.runOnUiThread { runBlocking {  resetUi() } }
        }

        activity.runOnUiThread { runBlocking {  updateUi() } }
    }

    private fun writeEventsToFile(events: List<Map<String, String>>) {
        val resolver: ContentResolver = activity.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "events_${System.currentTimeMillis()}.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/Thesis/Camera")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        try {
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    val jsonEvents = events.joinToString(prefix = "[", postfix = "]", separator = ",") { eventData ->
                        """{"yData": "${eventData["yData"]}", "uData": "${eventData["uData"]}", "vData": "${eventData["vData"]}"}"""
                    }
                    outputStream.write(jsonEvents.toByteArray())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                uri?.let { resolver.update(it, contentValues, null, null) }
            }
        }
    }
}

private fun ByteBuffer.toByteArray(): ByteArray {
    val bytes = ByteArray(remaining())
    get(bytes)
    return bytes

}
