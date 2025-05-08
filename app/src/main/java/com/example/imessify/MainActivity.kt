package com.example.imessify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.imessify.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

// Interface for drawer functionality
interface DrawerInterface {
    fun openDrawer()
    fun closeDrawer()
    fun isDrawerOpen(): Boolean
}

class MainActivity : AppCompatActivity(), DrawerInterface {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up bottom navigation
        setupBottomNavigation()
        
        // Load ContactsFragment as default
        if (savedInstanceState == null) {
            loadFragment(ContactsFragment())
            // Set the selected item in the navigation bar
            binding.navView.selectedItemId = R.id.navigation_contacts
        }
    }
    
    private fun setupBottomNavigation() {
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_messages -> {
                    loadFragment(MessagesFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_contacts -> {
                    loadFragment(ContactsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
    
    // Implement DrawerInterface methods
    override fun openDrawer() {
        // Implement drawer opening logic if needed
        // Example: binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    
    override fun closeDrawer() {
        // Implement drawer closing logic if needed
        // Example: binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
    
    override fun isDrawerOpen(): Boolean {
        // Implement drawer state checking
        // Example: return binding.drawerLayout.isDrawerOpen(GravityCompat.START)
        return false
    }
}
