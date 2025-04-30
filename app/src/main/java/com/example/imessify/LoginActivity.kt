package com.example.imessify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.os.Build
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvCreateAccount: TextView

    // Retrofit service
    private val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://imessify.x10.mx/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Make status bar transparent
        setupTransparentStatusBar()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Check if user is already logged in
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (sharedPref.contains("user_id")) {
            // User is already logged in, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Initialize views
        etEmail = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvCreateAccount = findViewById(R.id.tvForgotPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                }
                !isValidEmail(email) -> {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Call API to authenticate user
                    loginUser(email, password)
                }
            }
        }

        // Navigate to Create Account screen
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        // Show loading state
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        // Log the attempt (for debugging only)
        Log.d("LoginActivity", "Attempting login with email: $email")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)

                // Log the request object
                Log.d("LoginActivity", "Sending login request with email: $email")

                val response = apiService.loginUser(request)

                // Log the response
                Log.d("LoginActivity", "Login response received: success=${response.success}, message=${response.message}")

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                        // Save user info to SharedPreferences
                        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            response.username?.let { putString("username", it) }
                            response.userId?.let { putInt("user_id", it) }
                            apply()
                        }

                        // Navigate to MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close LoginActivity
                    } else {
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                        Log.w("LoginActivity", "Login failed: ${response.message}")
                    }

                    // Reset button
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login_button)
                }
            } catch (e: IOException) {
                Log.e("LoginActivity", "Network error during login", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Network error: Check your connection", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login_button)
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Unexpected error during login", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login_button)
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setupTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = android.graphics.Color.WHITE
            window.insetsController?.apply {
                setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
    }
}