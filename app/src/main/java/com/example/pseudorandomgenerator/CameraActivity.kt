package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.data_generator.AudioDataGenerator
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import com.example.utilities.ByteArrayListXOR
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import com.example.utilities.LeastSignificantBits
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var cameraData = ByteArray(0)
    private var audioData = ByteArray(0)
    private var isUsed = false

    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarCamera.max = EnvVariables.DESIRED_LENGTH

        initListeners()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initListeners() {
        binding.btnCameraStart.setOnClickListener {
            if (!isUsed) {
                startCamera()
                binding.btnCameraStart.text = "STOP"

            } else {
                stopCamera()
                binding.btnCameraStart.text = "START"

                if (smallestArraySize() >= EnvVariables.DESIRED_LENGTH) {
                    saveData()
                    resetData()
                    resetUiElements()
                }
            }

            isUsed = !isUsed
        }
    }

    private fun saveData() {
        
        val list = listOf(
            cameraData.slice(0 until EnvVariables.DESIRED_LENGTH).toByteArray(),
            audioData.slice(0 until EnvVariables.DESIRED_LENGTH).toByteArray()
        )
        val xor = ByteArrayListXOR.xor(list)

        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(xor),
            table = "camera"
        )

        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(cameraData.slice(0 until EnvVariables.DESIRED_LENGTH).toByteArray()),
            table = "camera_alone"
        )

        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(audioData.slice(0 until EnvVariables.DESIRED_LENGTH).toByteArray()),
            table = "camera_audio_alone"
        )
    }

    private fun resetData() {
        cameraData = ByteArray(0)
        audioData = ByteArray(0)
    }

    private fun resetUiElements() {
        binding.progressBarCamera.progress = 0
        binding.txtCameraBarPercent.text = "0%"
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgressInUi() {
        binding.progressBarCamera.progress = smallestArraySize()
        val percentage =
            (binding.progressBarCamera.progress.toFloat() / binding.progressBarCamera.max.toFloat()) * 100
        binding.txtCameraBarPercent.text = "${percentage.toInt()}%"
    }

    private fun startCamera() {
        // Almost all is copy pasted from the android development example.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Image Analysis
            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        // copy pasted from android development example.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
            } catch (exc: Exception) {
                Log.e(TAG, "Use case unbinding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun smallestArraySize(): Int {
        val arrays = listOf(
            cameraData,
            audioData
        )
        return arrays.minBy { it.size }.size
    }

    inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(image: ImageProxy) {
            if (cameraData.size < EnvVariables.DESIRED_LENGTH) {
                val average = getAverageValueOfTheFrame(image)
                val imageDataFractional: Long = getFractionalPartAsLong(average)

                cameraData += LeastSignificantBits.modWithPrimeAndGet8LSB(imageDataFractional)
            }

            if (audioData.size < EnvVariables.DESIRED_LENGTH) {
                val audio = AudioDataGenerator(this@CameraActivity).getAudioDataAverage()

                if (audio.toString() != "-Infinity") {
                    val audioFractional = getFractionalPartAsLong(audio.toString())
                    audioData += LeastSignificantBits.modWithPrimeAndGet8LSB(audioFractional)
                }
            }

            runOnUiThread {
                if (smallestArraySize() >= EnvVariables.DESIRED_LENGTH) {
                    saveData()
                    resetData()
                    resetUiElements()
                }
                updateProgressInUi()
            }

            image.close()
        }

        private fun getFractionalPartAsLong(data: String) = data.split(".")[1].toLong()

        private fun getAverageValueOfTheFrame(image: ImageProxy): String {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }

            return pixels.average().toString()
        }

        private fun ByteBuffer.toByteArray(): ByteArray {
            // Copy pasted from the android development example.
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }
    }
}