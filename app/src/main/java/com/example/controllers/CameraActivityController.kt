package com.example.controllers

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
    private val dataHolder: DataHolder<ByteArray>,
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
        val bitmap = imageData.toBitmap()

        val result = getBits(bitmap)

        dataHolder.addElementToList(camera, result)

        if (dataHolder.getListSize(camera) == Constants.DESIRED_LENGTH_CAMERA) {
            saveData(dataHolder.getListByName(camera))
            dataHolder.clearAllLists()
            activity.runOnUiThread { runBlocking {  resetUi() } }
        }

        activity.runOnUiThread { runBlocking {  updateUi() } }
    }

    private fun getBits(bitmap: Bitmap): ByteArray {
        return ByteArray(0)
    }


    private fun saveData(events: List<ByteArray>) {

    }
}
