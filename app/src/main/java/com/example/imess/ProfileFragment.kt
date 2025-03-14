package com.example.imess

import android.content.Intent
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load profile image with Glide (placeholder used here)
        Glide.with(this)
            .load(R.drawable.ic_launcher_foreground) // Replace with actual image URL or resource
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.profileImage)

        // Set user details (replace with real data retrieval)
        setupUserProfile()

        // Setup logout button
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun setupUserProfile() {
        // This should be replaced with actual data from your authentication system
        binding.tvUsername.text = "Manaois, Dayap, Butial"
        binding.tvEmail.text = "sample@gmail.com"
        binding.tvPhone.text = "09123456789"
        binding.tvLocation.text = "Antipolo City, Rizal"
    }

    private fun logoutUser() {
        // Implement your logout logic here
        // For example, clear user session and redirect to login activity

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