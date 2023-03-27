package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.controllers.TypingActivityController
import com.example.pseudorandomgenerator.databinding.ActivityTypeingBinding
import com.example.utilities.*
import kotlinx.coroutines.runBlocking

class TypingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTypeingBinding
    private val keystroke = "keystroke"
    private val time = "time"

    private val sources = listOf(
        keystroke,
        time
    )

    private lateinit var dataHolder: DataHolder<Long>
    private lateinit var collector: TypingActivityController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarTimeTyping.max = Constants.DESIRED_LENGTH
        initDataHolder()
        initCollector()
        runBlocking { collector.start() }
    }

    private fun initCollector() {
        collector = TypingActivityController(
            this,
            dataHolder,
            sources,
            ::updateUiElements,
            ::resetUiElements,
            binding
        )
    }

    private fun initDataHolder() {
        dataHolder = DataHolder()
        for (name in sources)
            dataHolder.createList(name)
    }

    @SuppressLint("SetTextI18n")
    fun updateUiElements() {
        binding.progressBarTimeTyping.progress = dataHolder.getMinListSize()

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