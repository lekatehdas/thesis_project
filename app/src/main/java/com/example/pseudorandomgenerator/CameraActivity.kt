package com.example.pseudorandomgenerator

import android.os.Bundle
import android.view.TextureView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {
    private lateinit var cameraPreview: TextureView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStart: Button
    private lateinit var txtProgressBar: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        initViews()
        initListeners()
    }

    private fun initListeners() {

    }

    private fun initViews() {
        cameraPreview = findViewById(R.id.textureViewCamera)
        progressBar = findViewById(R.id.progressBarCamera)
        btnStart = findViewById(R.id.btnCameraStart)
        txtProgressBar = findViewById(R.id.txtCameraBarPercent)
    }
}