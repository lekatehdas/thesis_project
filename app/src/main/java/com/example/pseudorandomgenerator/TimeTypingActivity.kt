package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView

class TimeTypingActivity : AppCompatActivity() {
    private lateinit var editTxtTimeTyping: EditText
    private lateinit var timeTypingProgressBar: ProgressBar
    private lateinit var txtTimeTypingBarPercent: TextView
    private var generatedData = ""

    private val textWatcher = object : TextWatcher {
        @SuppressLint("SetTextI18n")
        override fun afterTextChanged(s: Editable?) {
            if (generatedData.length > EnvVariables.DESIRED_LENGTH) {
                TODO("When desired length achieved, trim the end and save the data to the db. Notify the user That ")
            }

            generatedData += (System.currentTimeMillis() % 1000).toString()

            timeTypingProgressBar.max = EnvVariables.DESIRED_LENGTH
            timeTypingProgressBar.progress = generatedData.length

            val percentage = (timeTypingProgressBar.progress.toFloat() / timeTypingProgressBar.max.toFloat()) * 100
            txtTimeTypingBarPercent.text = "${percentage.toInt()}%"
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_typeing)


        initViews()
        initListeners()
    }

    private fun initListeners() {
        editTxtTimeTyping.addTextChangedListener(textWatcher)
    }

    private fun initViews() {
        editTxtTimeTyping = findViewById(R.id.editTxtTimeTyping)
        timeTypingProgressBar = findViewById(R.id.progressBarTimeTyping)
        txtTimeTypingBarPercent = findViewById(R.id.txtTimeTypingBarPercent)
    }
}