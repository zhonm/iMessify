package com.example.imessify

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imessify.adapters.MessagesAdapter
import com.example.imessify.databinding.ActivityMessageChatBinding
import com.example.imessify.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MessageChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageChatBinding
    private var contactId: Int = -1
    private var contactName: String = ""
    private var currentUserId: Int = -1
    private lateinit var messagesAdapter: MessagesAdapter
    
    // Auto-refresh handler for messages
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 5000L // 5 seconds
    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadMessages()
            handler.postDelayed(this, refreshInterval)
        }
    }
    
    // API Service for messages
    private val apiService: com.example.imessify.api.ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://imessify.x10.mx/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(com.example.imessify.api.ApiService::class.java)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user ID from shared preferences
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getInt("user_id", -1)
        
        if (currentUserId == -1) {
            Toast.makeText(this, "Please log in to send messages", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Get contact data from intent
        contactId = intent.getIntExtra("CONTACT_ID", -1)
        contactName = intent.getStringExtra("CONTACT_NAME") ?: ""

        // Set up the UI
        setupUI()
        
        // Load messages for this contact
        loadMessages()
        
        // Start periodic message refresh
        handler.postDelayed(refreshRunnable, refreshInterval)
    }
    
    private fun setupUI() {
        // Set up action bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = contactName
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        // Set up recycler view
        messagesAdapter = MessagesAdapter(currentUserId)
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessageChatActivity).apply {
                stackFromEnd = true // Scrolls to bottom of messages
            }
            adapter = messagesAdapter
        }
        
        // Set up send button
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.messageEditText.text?.clear()
                hideKeyboard()
            }
        }
    }

    private fun loadMessages() {
        if (contactId <= 0) {
            // If we don't have a valid contact ID, try to find it by username
            if (contactName.isNotEmpty()) {
                findContactIdByName(contactName)
            }
            return
        }
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getMessages(currentUserId, contactId)
                
                withContext(Dispatchers.Main) {
                    if (response.success) {
                        val messages = response.messages ?: emptyList()
                        
                        // Update UI with messages
                        messagesAdapter.updateMessages(messages)
                        
                        // Scroll to bottom if there are messages
                        if (messages.isNotEmpty()) {
                            binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
                        }
                    } else {
                        Log.e("MessageChatActivity", "Failed to load messages: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("MessageChatActivity", "Error loading messages", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MessageChatActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun findContactIdByName(name: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // This implementation assumes your API has a method to find user by username
                val response = apiService.findUserByUsername(name)
                
                withContext(Dispatchers.Main) {
                    if (response.success && response.userId != null) {
                        contactId = response.userId
                        loadMessages()
                    } else {
                        Toast.makeText(
                            this@MessageChatActivity,
                            "User not found: $name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MessageChatActivity", "Error finding contact", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MessageChatActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun sendMessage(text: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.sendMessage(
                    currentUserId,
                    contactName,
                    text
                )
                
                withContext(Dispatchers.Main) {
                    if (response.success) {
                        // Message sent successfully, reload messages to see the new message
                        loadMessages()
                    } else {
                        Toast.makeText(
                            this@MessageChatActivity,
                            response.message ?: "Failed to send message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MessageChatActivity", "Error sending message", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MessageChatActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
    
    override fun onPause() {
        super.onPause()
        // Stop message refresh when activity is not visible
        handler.removeCallbacks(refreshRunnable)
    }
    
    override fun onResume() {
        super.onResume()
        // Restart message refresh when activity becomes visible
        loadMessages()
        handler.postDelayed(refreshRunnable, refreshInterval)
    }
}
