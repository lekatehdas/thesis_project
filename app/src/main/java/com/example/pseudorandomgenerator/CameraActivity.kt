package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.controllers.CameraActivityController
import com.example.pseudorandomgenerator.databinding.ActivityCameraBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private val camera = "cameraData"

    private val sources = listOf(
        camera
    )

    private lateinit var dataHolder: DataHolder
    private lateinit var collector: CameraActivityController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarCamera.max = Constants.DESIRED_LENGTH

        initDataHolder()
        initCollector()
        initListeners()
    }

    private fun initCollector() {
        collector = CameraActivityController(
            dataHolder,
            sources,
            this,
            binding,
            ::updateProgressInUi,
            ::resetUiElements
        )
    }

    private fun initDataHolder() {
        dataHolder = DataHolder()
        for (name in sources)
            dataHolder.initializeList(name)
    }

    private fun initListeners() {
        binding.btnCameraStart.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                collector.start()

            if (!isChecked) {
                collector.stop()
            }
        }
    }

    private suspend fun resetUiElements() {
        binding.progressBarCamera.progress = 0
        binding.txtCameraBarPercent.text = "0%"
        binding.btnCameraStart.isChecked = false
    }

    @SuppressLint("SetTextI18n")
    private suspend fun updateProgressInUi() {
        binding.progressBarCamera.progress = dataHolder.getSizeOfSmallestList()

        val percentage =
            (binding.progressBarCamera.progress.toFloat() / binding.progressBarCamera.max.toFloat()) * 100
        binding.txtCameraBarPercent.text = "${percentage.toInt()}%"
    }
}