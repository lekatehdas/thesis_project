package com.example.pseudorandomgenerator

import AudioActivityController
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pseudorandomgenerator.databinding.ActivityAudioBinding
import com.example.utilities.Constants
import com.example.utilities.DataHolder
import kotlinx.coroutines.*

class AudioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioBinding
    private val audio = "audio"

    private lateinit var dataHolder: DataHolder<ByteArray>

    private lateinit var collector: AudioActivityController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarAudio.max = Constants.DESIRED_LENGTH
        initDataHolder()
        initCollector()
        initListeners()
    }

    private fun initCollector() {
        collector = AudioActivityController(
            this,
            dataHolder,
            audio,
            ::updateProgressInUi,
            ::resetUiElements
        )
    }

    private fun initDataHolder() {
        dataHolder = DataHolder()
        dataHolder.createList(audio)
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.btnAudioStart.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                CoroutineScope(Dispatchers.Main).launch {
                    collector.start()
                }
            if (!isChecked)
                collector.stop()
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun resetUiElements() {
        binding.progressBarAudio.progress = 0
        binding.txtAudioPercent.text = "0%"
        binding.btnAudioStart.text = "START"
        binding.btnAudioStart.isChecked = false //TODO commented out for infinite loop.
    }

    @SuppressLint("SetTextI18n")
    private suspend fun updateProgressInUi() {
        binding.progressBarAudio.progress = dataHolder.getMinListSize()

        val percentage = (
                binding.progressBarAudio.progress.toFloat() /
                        binding.progressBarAudio.max.toFloat()) * 100
        binding.txtAudioPercent.text = "${percentage.toInt()}%"
    }
}