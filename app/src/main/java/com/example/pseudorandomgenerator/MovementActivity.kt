package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.controllers.MovementActivityController
import com.example.pseudorandomgenerator.databinding.ActivityMovementBinding
import com.example.utilities.*

class MovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovementBinding

    private lateinit var controller: MovementActivityController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityMovementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarMovement.max = Constants.DESIRED_LENGTH

        initController()
        initListeners()
    }

    private fun initController() {
        controller = MovementActivityController(
            this,
            ::updateUiElements,
            ::resetUiElements
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.btnMovementStart.setOnClickListener {
            binding.btnMovementStart.text = "IN PROGRESS"
            controller.start()
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun updateUiElements(size: Int) {
        binding.progressBarMovement.progress = size

        val percentage =
            (binding.progressBarMovement.progress.toFloat() / binding.progressBarMovement.max.toFloat()) * 100
        binding.txtMovementPercent.text = "${percentage.toInt()}%"
    }

    @SuppressLint("SetTextI18n")
    private suspend fun resetUiElements() {
        binding.btnMovementStart.text = "START"
        binding.txtMovementPercent.text = "0%"
        binding.progressBarMovement.progress = 0
    }
}