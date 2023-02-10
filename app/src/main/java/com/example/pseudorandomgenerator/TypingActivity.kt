package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.collectors.TypingDataCollector
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.data_processors.ByteArrayListXOR
import com.example.data_processors.LeastSignificantBits
import com.example.pseudorandomgenerator.databinding.ActivityTypeingBinding
import com.example.utilities.*
import java.math.BigInteger

class TypingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTypeingBinding
    private val keystroke = "keystrokeData"
    private val time = "timeData"

    private val sources = listOf(
        keystroke,
        time
    )

    private lateinit var dataHolder: DataHolder
    private lateinit var collector: TypingDataCollector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarTimeTyping.max = Constants.DESIRED_LENGTH
        initDataHolder()
        initCollector()
        collector.start()
        }

    private fun initCollector() {
        collector = TypingDataCollector(dataHolder, sources, ::updateUiElements, ::resetUiElements, binding.editTxtTimeTyping)
    }

    private fun initDataHolder() {
        dataHolder = DataHolder()
        for (name in sources)
            dataHolder.initializeArray(name)
    }

    @SuppressLint("SetTextI18n")
    fun updateUiElements() {
        binding.progressBarTimeTyping.progress = dataHolder.getSizeOfSmallestArray()

        val percentage =
            (binding.progressBarTimeTyping.progress.toFloat() / binding.progressBarTimeTyping.max.toFloat()) * 100
        binding.txtTimeTypingBarPercent.text = "${percentage.toInt()}%"
    }

    private fun resetUiElements() {
        binding.editTxtTimeTyping.text.clear()
        binding.editTxtTimeTyping.clearFocus()
        binding.progressBarTimeTyping.progress = 0
        binding.txtTimeTypingBarPercent.text = "0%"
    }
}