package com.example.imess

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.imess.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Instagram handles (without the @)
    private val instagramHandles = listOf(
        "makie_mk",     // For the first button
        "davidhaydenx",  // For the second button
        "ayubutial"      // For the third button
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load profile image with Glide
        setupProfileImage()

        // Set user details
        setupUserProfile()

        // Setup Instagram buttons
        setupInstagramButtons()

        // Setup logout button
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun setupProfileImage() {
        Glide.with(this)
            .load(R.drawable.pic01)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.profileImage)
    }

    private fun setupUserProfile() {
        binding.tvUsername.text = "Manaois, Dayap, Butial"
        binding.tvEmail.text = "sample@gmail.com"
        binding.tvPhone.text = "09123456789"
        binding.tvLocation.text = "Antipolo City, Rizal"
    }

    private fun setupInstagramButtons() {
        binding.btnInstagram1.setOnClickListener {
            openInstagramProfile(instagramHandles[0])
        }

        binding.btnInstagram2.setOnClickListener {
            openInstagramProfile(instagramHandles[1])
        }

        binding.btnInstagram3.setOnClickListener {
            openInstagramProfile(instagramHandles[2])
        }
    }

    private fun openInstagramProfile(username: String) {
        try {
            // Try to open in Instagram app first
            val uri = Uri.parse("http://instagram.com/_u/$username")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")
            startActivity(intent)
        } catch (e: Exception) {
            // If Instagram app isn't installed, open in browser
            val uri = Uri.parse("http://instagram.com/$username")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun logoutUser() {
        // Navigate to LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}