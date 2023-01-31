package com.example.pseudorandomgenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pseudorandomgenerator.databinding.ActivityMainBinding
import com.example.utilities.PermissionHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionHelper = PermissionHelper(this)
        permissionHelper.checkAndAskPermissions()

        initListeners()
    }

    private fun initListeners() {
        binding.btnTyping.setOnClickListener {
            val intent = Intent(this, TypingActivity::class.java)
            startActivity(intent)
        }

        binding.btnWifi.setOnClickListener {
            val intent = Intent(this, WifiInfoActivity::class.java)
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

        binding.btnSpeech.setOnClickListener {
            val intent = Intent(this, SpeechToTextActivity::class.java)
            startActivity(intent)
        }

        binding.btnTraffic.setOnClickListener {
            val intent = Intent(this, NetworkTrafficActivity::class.java)
            startActivity(intent)
        }
    }
}