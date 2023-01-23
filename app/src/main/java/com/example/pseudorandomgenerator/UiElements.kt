package com.example.pseudorandomgenerator

import android.content.Context
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UiElements(private val activity: AppCompatActivity) {
    lateinit var btnMovement: Button;
    lateinit var btnWifi: Button;
    lateinit var btnBluetooth: Button;
    lateinit var btnTimeButton: Button;
    lateinit var btnTimeTyping: Button;
    lateinit var btnCamera: Button;
    lateinit var btnSpeech: Button;

    fun initViews() {
        btnMovement = activity.findViewById(R.id.btnMovement)
        btnWifi = activity.findViewById(R.id.btnWifi)
        btnBluetooth = activity.findViewById(R.id.btnBluetooth)
        btnTimeButton = activity.findViewById(R.id.btnTimeButton)
        btnTimeTyping = activity.findViewById(R.id.btnTimeTyping)
        btnCamera = activity.findViewById(R.id.btnCamera)
        btnSpeech = activity.findViewById(R.id.btnSpeech)
    }
}