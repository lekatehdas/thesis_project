package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import com.example.utilities.StringTruncator
import com.google.firebase.database.FirebaseDatabase

class TimeTypingActivity : AppCompatActivity() {
    private lateinit var editTxtTimeTyping: EditText
    private lateinit var timeTypingProgressBar: ProgressBar
    private lateinit var txtTimeTypingBarPercent: TextView
    private var generatedData = ""

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (generatedData.length >= EnvVariables.DESIRED_LENGTH) {
                saveData()
                resetUiElements()
                resetData()
            }

            updateUiElements()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiElements() {
        generatedData += (System.currentTimeMillis() % 1000).toString()
        timeTypingProgressBar.progress = generatedData.length

        val percentage =
            (timeTypingProgressBar.progress.toFloat() / timeTypingProgressBar.max.toFloat()) * 100
        txtTimeTypingBarPercent.text = "${percentage.toInt()}%"
    }

    private fun resetData() {
        generatedData = ""
    }

    private fun resetUiElements() {
        timeTypingProgressBar.progress = 0
        txtTimeTypingBarPercent.text = "0%"
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "time_typing"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_typeing)

        initViews()
        timeTypingProgressBar.max = EnvVariables.DESIRED_LENGTH

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