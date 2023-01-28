package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.pseudorandomgenerator.databinding.ActivityTimeBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import com.example.utilities.StringTruncator
import com.google.firebase.database.FirebaseDatabase

class TimeButtonActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTimeBinding

    private var generatedData = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarTimeButton.max = EnvVariables.DESIRED_LENGTH

        initListeners()
    }

    private fun initListeners() {
        binding.btnTimeButton.setOnClickListener {
            generatedData += (System.currentTimeMillis() % 1000).toString()

            if (generatedData.length >= EnvVariables.DESIRED_LENGTH) {
                saveData()
                resetData()
                resetUiElements()
            } else {
                updateUiElements()
            }
        }
    }

    private fun resetData() {
        generatedData = ""
    }

    private fun resetUiElements() {
        binding.progressBarTimeButton.progress = 0
        binding.txtTimeButtonBarPercent.text = "0%"
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "time_button"
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiElements() {
        binding.progressBarTimeButton.progress = generatedData.length

        val percentage = (binding.progressBarTimeButton.progress.toFloat() / binding.progressBarTimeButton.max.toFloat()) * 100
        binding.txtTimeButtonBarPercent.text = "${percentage.toInt()}%"
    }
}