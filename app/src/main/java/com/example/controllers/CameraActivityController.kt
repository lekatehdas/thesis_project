package com.example.controllers

import android.app.Activity
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.data_gatherers.AudioDataGatherer
import com.example.data_gatherers.CameraDataGatherer
import com.example.data_processors.ByteArrayListXOR
import com.example.data_processors.LeastSignificantBits
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
    private val audioGatherer = AudioDataGatherer(activity)
    private val camera = sources[0]
    private val audio = sources[1]

    fun start() {
        cameraGatherer.startCamera()
    }

    fun stop() {
        cameraGatherer.stopCamera()
    }

    private fun onImageData(imageData: String) {
        val imageLong = getFractionalPartAsLong(imageData)
        val imageByte = LeastSignificantBits.modWithPrimeAndGet8LSB(imageLong)
        dataHolder.concatArray(camera, imageByte)

        val audioData = audioGatherer.getAudioDataAverage()
        if (audioData.isFinite()) {
            val audioLong = getFractionalPartAsLong(audioData.toString())
            val audioByte = LeastSignificantBits.modWithPrimeAndGet8LSB(audioLong)
            dataHolder.concatArray(audio, audioByte)
        }

        if (dataHolder.getSizeOfSmallestArray() >= Constants.DESIRED_LENGTH) {
            saveData()
            resetData()
            activity.runOnUiThread { runBlocking {  resetUi() } }
        }

        activity.runOnUiThread { runBlocking {  updateUi() } }
    }

    private fun saveData() {

        val list = dataHolder.getAllArrays()
        val xor = ByteArrayListXOR.combineByteArraysThroughXOR(list)

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(xor),
            table = "camera"
        )

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(dataHolder.getArray(camera)),
            table = "camera_alone"
        )

        FirebaseDataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(dataHolder.getArray(audio)),
            table = "camera_audio_alone"
        )
    }

    private fun resetData() {
        dataHolder.resetData()
    }

    private fun getFractionalPartAsLong(data: String) = data.split(".")[1].toLong()
}