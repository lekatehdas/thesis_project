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
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var generatedData = ""
    private var isUsed = false

    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarCamera.max = EnvVariables.DESIRED_LENGTH

        useCamera()

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun useCamera() {
        binding.btnCameraStart.setOnClickListener {
            if (!isUsed) {
                startCamera()
                binding.btnCameraStart.text = "STOP"
            } else {
                stopCamera()
                binding.btnCameraStart.text = "START"
                if (generatedData.length >= EnvVariables.DESIRED_LENGTH) {
                    println(generatedData.substring(0, EnvVariables.DESIRED_LENGTH))

                    generatedData = ""
                    binding.progressBarCamera.progress = 0
                    binding.txtCameraBarPercent.text = "0%"
                }
            }
            isUsed = !isUsed
        }
    }

    private fun startCamera() {
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
        @SuppressLint("SetTextI18n")
        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }

            val average = pixels.average().toString()
            val fractional: Long = average.split(".")[1].toLong()

            val hexString = fractional.toString(16)
            val char = hexToAscii(hexString)

            if (char.isNotEmpty()) {
                generatedData = char + generatedData
            }

            binding.progressBarCamera.progress = generatedData.length

            runOnUiThread {
                val percentage = (binding.progressBarCamera.progress.toFloat() / binding.progressBarCamera.max.toFloat()) * 100
                binding.txtCameraBarPercent.text = "${percentage.toInt()}%"
            }

            image.close()
        }

        private fun hexToAscii(hex: String): String {
            if (hex.length < 8) {
                return ""
            }

            var decimal = BigInteger(hex, 16)
            val sb = StringBuilder()

            while (decimal > BigInteger.ZERO) {
                // get the last 8 digits of decimal
                val last8Digits = decimal.and(BigInteger.valueOf(0xff))

                // convert the last 8 digits to ascii
                val ascii = last8Digits.toInt()
                if (ascii in 32..126) sb.append(ascii.toChar())

                // divide the decimal by 2^8
                decimal = decimal.shiftRight(8)
            }
            return sb.reverse().toString()
        }

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }
    }
}