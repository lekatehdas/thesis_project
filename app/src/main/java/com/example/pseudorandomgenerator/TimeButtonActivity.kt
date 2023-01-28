package com.example.pseudorandomgenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.utilities.DataSaver
import com.example.utilities.EnvVariables
import com.example.utilities.StringTruncator
import com.google.firebase.database.FirebaseDatabase

class TimeButtonActivity: AppCompatActivity() {
    private lateinit var btnTime: Button;

    private lateinit var progressBar: ProgressBar;
    private lateinit var txtBarPercent: TextView;

    private var generatedData = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        initViews()
        progressBar.max = EnvVariables.DESIRED_LENGTH

        initListeners()
    }

    private fun initListeners() {
        btnTime.setOnClickListener {
            if (generatedData.length > EnvVariables.DESIRED_LENGTH) {
                saveData()
                resetUiElements()
                resetData()
            }

            generatedData += (System.currentTimeMillis() % 1000).toString()

            updateUiElements()
        }
    }

    private fun resetData() {
        generatedData = ""
    }

    private fun resetUiElements() {
        progressBar.progress = 0
        txtBarPercent.text = "0%"
    }

    private fun saveData() {
        DataSaver.saveData(
            data = generatedData,
            table = "time_button"
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateUiElements() {
        progressBar.progress = generatedData.length

        val percentage = (progressBar.progress.toFloat() / progressBar.max.toFloat()) * 100
        txtBarPercent.text = "${percentage.toInt()}%"
    }

    private fun initViews() {
        btnTime = findViewById(R.id.btnTimeButton)
        progressBar = findViewById(R.id.progressBarTimeButton)
        txtBarPercent = findViewById(R.id.txtTimeButtonBarPercent)
    }
}