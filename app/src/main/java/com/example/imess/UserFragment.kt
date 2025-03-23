package com.example.imess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imess.databinding.ActivityUserBinding

class UserFragment : AppCompatActivity() {

    private lateinit var binding : ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val imageId = intent.getIntExtra("imageId",R.drawable.pic1)

        binding.nameProfile.text = name
        binding.PhoneProfile.text = phone
        binding.profImage.setImageResource(imageId)
    }
}

