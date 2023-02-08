package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.pseudorandomgenerator.databinding.ActivitySpeechToTextBinding
import com.example.data_generator.SpeechDataGenerator
import com.example.utilities.*

class SpeechToTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpeechToTextBinding
    private val desiredLength = EnvVariables.DESIRED_LENGTH * 10

    private lateinit var speechDataGenerator: SpeechDataGenerator

    private val processor = ByteArrayProcessor()

    private var stringData = ByteArray(0)
    private var audioData = ByteArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeechToTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speechDataGenerator = SpeechDataGenerator(this)

        binding.progressBarSpeech.max = desiredLength

        binding.btnStartSpeech.setOnClickListener {
            speechDataGenerator.startVoiceRecognition()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val listOfData: List<ByteArray> =  speechDataGenerator.onActivityResult(requestCode, resultCode, data)

        if (!enoughStringData()) {
            getStringData(listOfData)
        }

        if (!enoughAudioData()) {
            getAudioData(listOfData)
        }

        updateUiElements()

        if (enoughData()) {
            saveData()
            resetData()
            resetUiElements()
            speechDataGenerator.startVoiceRecognition()
        } else {
            speechDataGenerator.startVoiceRecognition()
        }
    }

    private fun getAudioData(arrays: List<ByteArray>) {
        var rawAudioData = arrays[1]

        while (rawAudioData.size > desiredLength * 2) {
            rawAudioData = processor.process(rawAudioData)
        }

        audioData += rawAudioData
    }

    private fun getStringData(arrays: List<ByteArray>) {
        var rawStringData = arrays[0]

        while (rawStringData.size > desiredLength * 2) {
            rawStringData = processor.process(rawStringData)
        }

        stringData += rawStringData
    }

    private fun saveData() {
        val audio = audioData.slice(0 until desiredLength).toByteArray()
        val text = stringData.slice(0 until desiredLength).toByteArray()

        val lists = listOf(
            audio,
            text
        )

        val xor = ByteArrayListXOR.xor(lists)
        val result = ByteArrayToBinaryStringConverter.convert(xor)

        DataSaver.saveData(
            data = result,
            table = "speech"
        )

        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(text),
            table = "speech_audio_alone"
        )

        DataSaver.saveData(
            data = ByteArrayToBinaryStringConverter.convert(audio),
            table = "speech_text_alone"
        )
    }

    private fun enoughStringData() = stringData.size >= desiredLength

    private fun enoughAudioData() = audioData.size >= desiredLength

    private fun resetData() {
        stringData = ByteArray(0)
        audioData = ByteArray(0)
    }

    private fun resetUiElements() {
        binding.progressBarSpeech.progress = 0
        binding.txtProgressBarSpeechPercent.text = "0%"
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiElements() {
        binding.progressBarSpeech.progress = smallestArraySize()
        val percentage =
            (binding.progressBarSpeech.progress.toFloat() / binding.progressBarSpeech.max.toFloat()) * 100
        binding.txtProgressBarSpeechPercent.text = "${percentage.toInt()}%"
    }

    private fun smallestArraySize(): Int {
        val arrays = listOf(
            audioData,
            stringData
        )
        return arrays.minBy { it.size }.size
    }

    private fun enoughData(): Boolean {
        return smallestArraySize() >= desiredLength
    }
}