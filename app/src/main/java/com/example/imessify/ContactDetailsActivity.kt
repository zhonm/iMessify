package com.example.imessify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.imessify.databinding.ActivityContactDetailsBinding

class ContactDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val name = intent.getStringExtra("name") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val imageId = intent.getIntExtra("imageId", R.drawable.ic_person)

        // Set up views
        binding.contactName.text = name
        binding.contactPhone.text = phone
        
        Glide.with(this)
            .load(imageId)
            .centerCrop()
            .into(binding.contactImage)
            
        binding.backButton.setOnClickListener {
            // Use onBackPressedDispatcher instead of deprecated onBackPressed()
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
