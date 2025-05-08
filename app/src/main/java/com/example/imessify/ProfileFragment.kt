package com.example.imessify

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.imessify.databinding.FragmentProfileBinding

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

        // Setup back button listener to show logout dialog
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showLogoutConfirmationDialog()
                }
            }
        )

        // Load profile image with Glide
        setupProfileImage()

        // Set user details
        setupUserProfile()

        // Setup profile image click listener
        binding.profileImage.setOnClickListener {
            onProfileImageClick(it)
        }

        // Setup logout button
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    // Add the missing method that's being referenced in an onClick attribute
    fun onProfileImageClick(view: View) {
        // Show a dialog or launch an intent to change profile picture
        Toast.makeText(requireContext(), "Change profile picture", Toast.LENGTH_SHORT).show()

        // You can add more functionality here to change the profile picture
        // For example, show an options dialog to take a photo or choose from gallery
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

    private fun logoutUser() {
        // Navigate to LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_logout, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)

        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
        val btnLogout = dialogView.findViewById<TextView>(R.id.btnLogout)

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnLogout.setOnClickListener {
            logoutUser()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}