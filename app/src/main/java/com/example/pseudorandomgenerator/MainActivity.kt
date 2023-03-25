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
        binding.btnTyping.setOnClickListener { startActivity(TypingActivity::class.java) }
        binding.btnCamera.setOnClickListener { startActivity(CameraActivity::class.java) }
        binding.btnMovement.setOnClickListener { startActivity(MovementActivity::class.java) }
        binding.btnAudio.setOnClickListener { startActivity(AudioActivity::class.java) }
        binding.btnSpeech.setOnClickListener { startActivity(DrawActivity::class.java) }
    }

    private fun startActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
