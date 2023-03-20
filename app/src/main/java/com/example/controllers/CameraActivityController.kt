package com.example.controllers

import android.app.Activity
import com.example.data_gatherers.CameraDataGatherer
import com.example.data_processors.LeastSignificantBitExtractor
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import com.example.utilities.FirebaseDataSaver
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KSuspendFunction0

class CameraActivityController(
    private val dataHolder: DataHolder,
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

    private fun onImageData(imageData: Double) {
        val imageBit = LeastSignificantBitExtractor.extract(imageData)

        dataHolder.addToList(camera, imageBit)

        if (dataHolder.getSizeOfSmallestList() >= Constants.DESIRED_LENGTH) {
            saveData()
            resetData()
            activity.runOnUiThread { runBlocking {  resetUi() } }
        }

        activity.runOnUiThread { runBlocking {  updateUi() } }
    }

    private fun saveData() {
        FirebaseDataSaver.saveData(
            data = dataHolder.getList(camera),
            table = "camera"
        )
    }

    private fun resetData() {
        dataHolder.resetData()
    }
}