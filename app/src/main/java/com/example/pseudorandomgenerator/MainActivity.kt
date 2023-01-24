package com.example.pseudorandomgenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    /*
    Methods to be used:
        -Continuous camera feedback.

        -Geolocation, random spots from google maps and then select spot.
            -Need to normalise the data somehow.

        -Cesar cypher
            -Text to speech, record also the audio.
            -From the audio, create a mapping function so that it generates a unix char string
            -Xor this audio generated unix string together with what ever was spoken to the text.

        - Generally needed.
            - To save data to the DB together with timestamp and method name.
            Pref automatically when desired amount of data has been generated.
     */
    private lateinit var uiElements: UiElements

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uiElements = UiElements(this@MainActivity)
        uiElements.initViews()

        initListeners()
    }

    private fun initListeners() {
        uiElements.btnTimeButton.setOnClickListener {
            val intent = Intent(this, TimeButtonActivity::class.java)
            startActivity(intent)
        }

        uiElements.btnTimeTyping.setOnClickListener {
            val intent = Intent(this, TimeTypingActivity::class.java)
            startActivity(intent)
        }

        uiElements.btnWifi.setOnClickListener {
            val intent = Intent(this, WifiNoiseActivity::class.java)
            startActivity(intent)
        }

        uiElements.btnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        uiElements.btnMovement.setOnClickListener {
            val intent = Intent(this, MovementActivity::class.java)
            startActivity(intent)
        }
    }
}