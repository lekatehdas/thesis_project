package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pseudorandomgenerator.databinding.ActivityTextToScpeehBinding
import com.example.utilities.EnvVariables
import com.example.utilities.SpeechRecognitionManager
import com.example.utilities.StringTruncator
import com.google.firebase.database.FirebaseDatabase

class TextToSpeechActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextToScpeehBinding
    private lateinit var speechRecognitionManager: SpeechRecognitionManager
    private var generatedData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextToScpeehBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speechRecognitionManager = SpeechRecognitionManager(this)

        binding.progressBarSpeech.max = EnvVariables.DESIRED_LENGTH

        binding.btnStartSpeech.setOnClickListener {
            speechRecognitionManager.startVoiceRecognition()
        }
    }

    private fun enoughData(): Boolean {
        return generatedData.length >= EnvVariables.DESIRED_LENGTH
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        generatedData += speechRecognitionManager.onActivityResult(requestCode, resultCode, data)
        updateUiElements()

        if (enoughData()) {
            saveData()
            resetUiElements()
            resetData()
        }
    }

    private fun saveData() {
        val finalString = StringTruncator.truncate(generatedData, EnvVariables.DESIRED_LENGTH)
        val dbRef = FirebaseDatabase.getInstance().getReference("speech")
        dbRef.push().setValue(finalString)
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