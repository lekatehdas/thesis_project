package com.example.pseudorandomgenerator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class TimeActivity : AppCompatActivity() {
    private lateinit var btnTime1: Button;
    private lateinit var btnTime2: Button;
    private lateinit var btnTime3: Button;
    private lateinit var btnTime4: Button;

    private lateinit var progressBar: ProgressBar;
    private lateinit var content: TextView;

    private val generatedNumbers = ArrayList<Number>(100);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        initViews()
        initListeners()
    }

    private fun initListeners() {
        btnTime1.setOnClickListener {
            if (generatedNumbers.size == 100) {
                generatedNumbers.removeAt(0)
            }

            val currentTime = (System.nanoTime() % 10000)
            generatedNumbers.add(currentTime)
            progressBar.progress = generatedNumbers.size
            content.text = generatedNumbers.toString()
        }
    }

    private fun initViews() {
        btnTime1 = findViewById(R.id.btnTime1)
        btnTime2 = findViewById(R.id.btnTime2)
        btnTime3 = findViewById(R.id.btnTime3)
        btnTime4 = findViewById(R.id.btnTime4)
        progressBar = findViewById(R.id.progressBar)
        content = findViewById(R.id.txtWelcome)
    }
}