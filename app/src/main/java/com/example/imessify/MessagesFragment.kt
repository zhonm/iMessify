package com.example.imessify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imessify.adapters.ConversationsAdapter
import com.example.imessify.api.ApiService
import com.example.imessify.databinding.FragmentMessagesBinding
import com.example.imessify.api.Conversation
import com.example.imessify.api.ConversationsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessagesFragment : Fragment(), ConversationsAdapter.OnConversationClickListener {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var conversationsAdapter: ConversationsAdapter
    private var currentUserId: Int = 0
    
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 5000L // 5 seconds
    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchConversations()
            handler.postDelayed(this, refreshInterval)
        }
    }
    
    // Retrofit service
    private val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://imessify.x10.mx/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get user information from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getInt("user_id", 0)
        
        if (currentUserId == 0) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        setupUI()
        fetchConversations()
    }
    
    private fun setupUI() {
        // Setup RecyclerView
        binding.conversationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        conversationsAdapter = ConversationsAdapter(mutableListOf(), this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
        
        // Setup new message button
        binding.btnNewMessage.setOnClickListener {
            showNewMessageDialog()
        }
    }
    
    private fun showNewMessageDialog() {
        val dialog = NewMessageDialogFragment()
        dialog.setOnUsernameSelectedListener { username ->
            startChatWithUsername(username)
        }
        dialog.show(childFragmentManager, "NewMessageDialog")
    }
    
    private fun startChatWithUsername(username: String) {
        val intent = Intent(requireContext(), MessageChatActivity::class.java).apply {
            putExtra("CONTACT_NAME", username)
            // Note: We do not have a contact ID for a new conversation,
            // so MessageChatActivity will handle finding the user by username
        }
        startActivity(intent)
    }
    
    private fun fetchConversations() {
        if (currentUserId == 0) return
        
        _binding?.loadingProgressBar?.visibility = View.VISIBLE
        
        apiService.getConversations(currentUserId).enqueue(object : Callback<com.example.imessify.api.ConversationsResponse> {
            override fun onResponse(call: Call<com.example.imessify.api.ConversationsResponse>, response: Response<com.example.imessify.api.ConversationsResponse>) {
                if (!isAdded || _binding == null) return
                
                _binding?.loadingProgressBar?.visibility = View.GONE
                
                if (response.isSuccessful) {
                    val conversationsResponse = response.body()
                    if (conversationsResponse?.success == true) {
                        val conversations = conversationsResponse.conversations ?: emptyList()
                        
                        conversationsAdapter.updateConversations(conversations)
                        
                        // Show empty state if no conversations
                        if (conversations.isEmpty()) {
                            _binding?.emptyConversationsText?.visibility = View.VISIBLE
                            _binding?.conversationsRecyclerView?.visibility = View.GONE
                        } else {
                            _binding?.emptyConversationsText?.visibility = View.GONE
                            _binding?.conversationsRecyclerView?.visibility = View.VISIBLE
                        }
                    } else {
                        showEmptyState("${conversationsResponse?.message ?: "Failed to load conversations"}")
                    }
                } else if (response.code() == 404) {
                    // Handle 404 error specifically - likely API endpoint doesn't exist
                    showEmptyState("API endpoint not found. Please contact support.")
                    
                    // Redirect to login if this is a critical authentication issue
                    if (isAdded && context != null) {
                        redirectToLogin()
                    }
                } else {
                    showEmptyState("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<com.example.imessify.api.ConversationsResponse>, t: Throwable) {
                if (!isAdded || _binding == null) return
                
                _binding?.loadingProgressBar?.visibility = View.GONE
                showEmptyState("Network error: ${t.message}")
                
                // If network error persists, might need to relogin
                if (t.message?.contains("Unable to resolve host") == true) {
                    if (isAdded && context != null) {
                        redirectToLogin()
                    }
                }
            }
        })
    }
    
    private fun showEmptyState(errorMessage: String) {
        if (!isAdded || _binding == null) return
        
        if (context != null) {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
        
        _binding?.emptyConversationsText?.text = "No conversations\n$errorMessage"
        _binding?.emptyConversationsText?.visibility = View.VISIBLE
        _binding?.conversationsRecyclerView?.visibility = View.GONE
    }
    
    private fun redirectToLogin() {
        // Only proceed if the fragment is attached to prevent crashes
        if (!isAdded || context == null) return
        
        // Clear stored user credentials and redirect to login
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("LOGIN_ERROR", true)
        }
        startActivity(intent)
        requireActivity().finish()
    }
    
    override fun onConversationClick(conversation: Conversation) {
        // Open chat activity with this conversation
        val intent = Intent(requireContext(), MessageChatActivity::class.java).apply {
            putExtra("CONTACT_ID", conversation.otherUserId)
            putExtra("CONTACT_NAME", conversation.otherUsername)
        }
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        // Start periodic conversation fetching
        handler.postDelayed(refreshRunnable, refreshInterval)
    }
    
    override fun onPause() {
        super.onPause()
        // Stop periodic conversation fetching
        handler.removeCallbacks(refreshRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}