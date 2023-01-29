package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pseudorandomgenerator.databinding.ActivitySpeechToTextBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import com.example.data_generator.SpeechDataGenerator

class SpeechToTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpeechToTextBinding
    private lateinit var speechDataGenerator: SpeechDataGenerator
    private var generatedData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeechToTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speechDataGenerator = SpeechDataGenerator(this)

        binding.progressBarSpeech.max = EnvVariables.DESIRED_LENGTH

        binding.btnStartSpeech.setOnClickListener {
            speechDataGenerator.startVoiceRecognition()
        }
    }

    private fun enoughData(): Boolean {
        return generatedData.length >= EnvVariables.DESIRED_LENGTH
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        generatedData += speechDataGenerator.onActivityResult(requestCode, resultCode, data)
        updateUiElements()

        if (enoughData()) {
            saveData()
            resetUiElements()
            resetData()
        }
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "speech"
        )
    }

    private fun resetData() {
        generatedData = ""
    }

    private fun resetUiElements() {
        binding.progressBarSpeech.progress = 0
        binding.txtProgressBarSpeechPercent.text = "0%"
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiElements() {
        binding.progressBarSpeech.progress = generatedData.length
        val percentage =
            (binding.progressBarSpeech.progress.toFloat() / binding.progressBarSpeech.max.toFloat()) * 100
        binding.txtProgressBarSpeechPercent.text = "${percentage.toInt()}%"
    }
}