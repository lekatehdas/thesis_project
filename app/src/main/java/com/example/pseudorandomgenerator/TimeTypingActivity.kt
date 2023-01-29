package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.interfaces.KeystrokeCallback
import com.example.data_generator.KeystrokeDataGenerator
import com.example.pseudorandomgenerator.databinding.ActivityTimeTypeingBinding
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables

class TimeTypingActivity : AppCompatActivity(), KeystrokeCallback {
    private lateinit var binding: ActivityTimeTypeingBinding
    private val keystrokeTracker = KeystrokeDataGenerator(this)

    private var generatedData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeTypeingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarTimeTyping.max = EnvVariables.DESIRED_LENGTH

        initListeners()
    }

    private fun initListeners() {
        binding.editTxtTimeTyping.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (generatedData.length >= EnvVariables.DESIRED_LENGTH) {
                    saveData()
                    resetData()
                    resetUiElements()
                } else {
                    updateUiElements()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                keystrokeTracker.onTextChanged(s!!, start, before, count)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun updateUiElements() {
        binding.progressBarTimeTyping.progress = generatedData.length

        val percentage =
            (binding.progressBarTimeTyping.progress.toFloat() / binding.progressBarTimeTyping.max.toFloat()) * 100
        binding.txtTimeTypingBarPercent.text = "${percentage.toInt()}%"
    }

    private fun resetData() {
        generatedData = ""
    }

    private fun resetUiElements() {
        binding.editTxtTimeTyping.text.clear()
        binding.editTxtTimeTyping.clearFocus()
        binding.progressBarTimeTyping.progress = 0
        binding.txtTimeTypingBarPercent.text = "0%"
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "time_typing"
        )
    }

    override fun updateActivityData(output: String) {
        generatedData += output
    }
}