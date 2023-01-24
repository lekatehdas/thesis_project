package com.example.pseudorandomgenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pseudorandomgenerator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    /*
    Methods to be used:
        -Cesar cypher
            -Text to speech, record also the audio.
            -From the audio, create a mapping function so that it generates a unix char string
            -Xor this audio generated unix string together with what ever was spoken to the text.

        - Generally needed.
            - To save data to the DB together with timestamp and method name.
            Pref automatically when desired amount of data has been generated.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionHelper = PermissionHelper(this)
        permissionHelper.checkAndAskPermissions()

        initListeners()
    }

    private fun initListeners() {
        binding.btnTimeButton.setOnClickListener {
            val intent = Intent(this, TimeButtonActivity::class.java)
            startActivity(intent)
        }

        binding.btnTimeTyping.setOnClickListener {
            val intent = Intent(this, TimeTypingActivity::class.java)
            startActivity(intent)
        }

        binding.btnWifi.setOnClickListener {
            val intent = Intent(this, WifiNoiseActivity::class.java)
            startActivity(intent)
        }

        binding.btnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.btnMovement.setOnClickListener {
            val intent = Intent(this, MovementActivity::class.java)
            startActivity(intent)
        }
    }
}