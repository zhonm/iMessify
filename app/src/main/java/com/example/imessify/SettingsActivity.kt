package com.example.imessify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.imessify.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Account section
        binding.settingProfile.setOnClickListener {
            // Handle profile click
        }

        binding.settingSecurity.setOnClickListener {
            // Handle security click
        }

        // Chat section
        binding.settingNotifications.setOnClickListener {
            // Handle notifications click
        }

        binding.settingAppearance.setOnClickListener {
            // Handle appearance click
        }

        binding.settingChatFeatures.setOnClickListener {
            // Handle chat features click
        }

        // Support section
        binding.settingHelp.setOnClickListener {
            // Handle help click
        }

        binding.settingAbout.setOnClickListener {
            // Handle about click
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}