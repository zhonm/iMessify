package com.example.imessify

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.imessify.databinding.ActivityMainBinding
import com.example.imessify.databinding.NavigationDrawerBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerBinding: NavigationDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup drawer layout
        drawerLayout = binding.drawerLayout
        drawerBinding = NavigationDrawerBinding.bind(findViewById(R.id.navigation_drawer))

        // Setup drawer menu item clicks
        setupDrawerListeners()

        // Setup white status bar
        setupStatusBar()

        // Load MessagesFragment as default when app starts
        if (savedInstanceState == null) {
            loadFragment(MessagesFragment())
        }

        // Set up bottom navigation listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_messages -> {
                    loadFragment(MessagesFragment())
                    true
                }
                R.id.navigation_contacts -> {
                    loadFragment(ContactsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupDrawerListeners() {
        drawerBinding.navSettings.setOnClickListener {
            // Handle settings click
            drawerLayout.closeDrawer(GravityCompat.START)
            // TODO: Navigate to settings screen
        }

        drawerBinding.navRecentlyDeleted.setOnClickListener {
            // Handle recently deleted click
            drawerLayout.closeDrawer(GravityCompat.START)
            // TODO: Navigate to recently deleted screen
        }
    }

    // Method to be called by fragments to toggle drawer
    fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API level 30) and above
            window.statusBarColor = android.graphics.Color.WHITE
            window.insetsController?.apply {
                // Set light status bar with dark icons
                setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}