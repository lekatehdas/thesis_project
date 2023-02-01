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
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.experimental.and

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var cameraData = ByteArray(0)
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

                if (cameraData.size >= EnvVariables.DESIRED_LENGTH) {
                    saveData()
                    resetData()
                    resetUiElements()
                }
            }

            isUsed = !isUsed
        }
    }

    private fun saveData() {
        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(cameraData),
            table = "camera"
        )
    }

    private fun resetData() {
        cameraData = ByteArray(0)
    }

    private fun resetUiElements() {
        binding.progressBarCamera.progress = 0
        binding.txtCameraBarPercent.text = "0%"
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgressInUi() {
        binding.progressBarCamera.progress = cameraData.size
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

    inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(image: ImageProxy) {
            val average = getAverageValueOfTheFrame(image)
            val fractional: Long = average.split(".")[1].toLong()

            val modData = fractional % EnvVariables.PRIME_FOR_MOD

            val dataInBytes = modData.toByte()

            if (cameraData.size < EnvVariables.DESIRED_LENGTH)
                cameraData += byteArrayOf(dataInBytes and 0xff.toByte())

            runOnUiThread {
                updateProgressInUi()
            }

            image.close()
        }

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