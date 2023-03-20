package com.example.data_gatherers

import android.app.Activity
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.reflect.KFunction1

class CameraDataGatherer(
    private val activity: Activity,
    private val binding: ActivityCameraBinding,
    private val exportData: KFunction1<String, Unit>
) {

    private var cameraExecutor = Executors.newSingleThreadExecutor()
    private val TAG = "CameraDataGatherer"

    fun startCamera() {
        // Almost all is copy pasted from the android development example.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

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
                    activity as LifecycleOwner, cameraSelector, preview, imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(activity))
    }

    fun stopCamera() {
        // copy pasted from android development example.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
            } catch (exc: Exception) {
                Log.e(TAG, "Use case unbinding failed", exc)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(image: ImageProxy) {
            val average = getAverageValueOfTheFrame(image)

            exportData(average)

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
