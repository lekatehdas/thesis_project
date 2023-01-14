package com.example.pseudorandomgenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var btnMovement: Button;
    private lateinit var btnTime: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initListeners()
    }

    private fun initViews() {
        btnMovement = findViewById(R.id.btnMovement)
        btnTime = findViewById(R.id.btnTime)
    }

    private fun initListeners() {
        btnTime.setOnClickListener {
            val intent = Intent(this, TimeActivity::class.java)
            startActivity(intent)
        }

        btnMovement.setOnClickListener {
            val intent = Intent(this, MovementActivity::class.java)
            startActivity(intent)
        }
    }
}