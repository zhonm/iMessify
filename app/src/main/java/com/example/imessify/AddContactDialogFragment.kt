package com.example.imessify

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imessify.adapters.UserSearchAdapter
import com.example.imessify.api.ContactsApiService
import com.example.imessify.databinding.DialogAddContactBinding
import com.example.imessify.models.ContactRequest
import com.example.imessify.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddContactDialogFragment : DialogFragment(), UserSearchAdapter.OnUserClickListener {
    
    private var _binding: DialogAddContactBinding? = null
    private val binding get() = _binding!!
    
    private var userId: Int = -1
    private var selectedUser: User? = null
    private var searchJob: Job? = null
    
    private lateinit var searchAdapter: UserSearchAdapter
    private lateinit var apiService: ContactsApiService
    
    private var onContactAddedListener: ((Boolean) -> Unit)? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup UI components
        setupRecyclerView()
        setupListeners()
        setupSearchFunctionality()
        
        // Initialize the Add button as disabled initially
        binding.btnAdd.isEnabled = false
        binding.btnAdd.alpha = 0.5f
    }
    
    private fun setupRecyclerView() {
        searchAdapter = UserSearchAdapter(this)
        binding.searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            // Initially hidden until search is performed
            visibility = View.GONE
        }
        
        // Make sure the card containing the RecyclerView is also hidden
        binding.searchResultsCard.visibility = View.GONE
    }
    
    private fun setupListeners() {
        // Cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        // Add button
        binding.btnAdd.setOnClickListener {
            val user = selectedUser
            if (user == null) {
                Toast.makeText(context, "Please select a user first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val displayName = binding.etContactName.text.toString().trim()
            val nameToUse = if (displayName.isNotEmpty()) displayName else user.username
            
            addContact(nameToUse, user.username)
        }
    }
    
    private fun setupSearchFunctionality() {
        binding.etSearchUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Cancel any previous search job
                searchJob?.cancel()
                
                val query = s.toString().trim()
                if (query.length >= 2) {
                    // Show loading indicator
                    binding.searchProgressBar.visibility = View.VISIBLE
                    binding.emptySearchResults.visibility = View.GONE
                    binding.searchResultsCard.visibility = View.GONE
                    
                    // Delay the search to avoid too many API calls while typing
                    searchJob = lifecycleScope.launch {
                        delay(300)  // 300ms delay
                        searchUsers(query)
                    }
                } else {
                    // Clear search results if query is too short
                    searchAdapter.submitList(emptyList())
                    binding.searchResultsCard.visibility = View.GONE
                    binding.searchProgressBar.visibility = View.GONE
                    binding.emptySearchResults.visibility = View.VISIBLE
                    binding.emptySearchResults.text = "Search for users to add as contacts"
                    
                    // Reset selection when clearing search
                    resetSelection()
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun searchUsers(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.searchUsers(query)
                
                withContext(Dispatchers.Main) {
                    // Hide loading indicator regardless of result
                    binding.searchProgressBar.visibility = View.GONE
                    
                    if (response.success && response.users != null) {
                        val users = response.users.filter { it.id != userId }  // Filter out current user
                        
                        if (users.isNotEmpty()) {
                            searchAdapter.submitList(users)
                            binding.searchResultsRecyclerView.visibility = View.VISIBLE
                            binding.searchResultsCard.visibility = View.VISIBLE
                            binding.emptySearchResults.visibility = View.GONE
                        } else {
                            binding.searchResultsCard.visibility = View.GONE
                            binding.emptySearchResults.visibility = View.VISIBLE
                            binding.emptySearchResults.text = "No users found matching '$query'"
                        }
                    } else {
                        binding.searchResultsCard.visibility = View.GONE
                        binding.emptySearchResults.visibility = View.VISIBLE
                        binding.emptySearchResults.text = response.message ?: "Error searching for users"
                    }
                }
            } catch (e: Exception) {
                Log.e("AddContactDialog", "Error searching users", e)
                withContext(Dispatchers.Main) {
                    binding.searchProgressBar.visibility = View.GONE
                    binding.searchResultsCard.visibility = View.GONE
                    binding.emptySearchResults.visibility = View.VISIBLE
                    binding.emptySearchResults.text = "Error: ${e.message}"
                }
            }
        }
    }
    
    override fun onUserClick(user: User) {
        Log.d("AddContactDialog", "User selected: ${user.username}")
        selectedUser = user
        
        // Update UI to show selection
        binding.etContactName.setText(user.username)
        binding.etContactName.hint = "Custom name for ${user.username}"
        
        // Show selection information
        binding.selectedContactInfo.text = "Selected: @${user.username}"
        binding.selectedContactInfo.visibility = View.VISIBLE
        
        // Make sure the Add button is enabled and fully visible
        binding.btnAdd.isEnabled = true
        binding.btnAdd.alpha = 1.0f
        
        // Hide search results after selection
        binding.searchResultsCard.visibility = View.GONE
        binding.emptySearchResults.visibility = View.GONE
        
        // Clear search field
        binding.etSearchUsername.text?.clear()
        
        // Toast to confirm selection
        Toast.makeText(requireContext(), "User ${user.username} selected", Toast.LENGTH_SHORT).show()
    }
    
    private fun resetSelection() {
        selectedUser = null
        binding.btnAdd.isEnabled = false
        binding.btnAdd.alpha = 0.5f
        binding.selectedContactInfo.visibility = View.GONE
    }
    
    private fun updateAddButtonState(enabled: Boolean) {
        binding.btnAdd.isEnabled = enabled
        if (enabled) {
            binding.btnAdd.alpha = 1.0f
            binding.btnAdd.backgroundTintList = context?.getColorStateList(com.google.android.material.R.color.design_default_color_primary)
        } else {
            binding.btnAdd.alpha = 0.5f
            binding.btnAdd.backgroundTintList = context?.getColorStateList(com.google.android.material.R.color.design_default_color_secondary)
        }
    }
    
    private fun addContact(name: String, username: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = ContactRequest(
                    userId = userId,
                    name = name,
                    username = username,
                    imageUrl = null // Using default image
                )

                val response = apiService.createContact(request)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show()
                        onContactAddedListener?.invoke(true)
                        dismiss()
                    } else {
                        val errorMessage = response.message ?: "Failed to add contact"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        onContactAddedListener?.invoke(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("AddContactDialog", "Error adding contact", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    onContactAddedListener?.invoke(false)
                }
            }
        }
    }
    
    fun setContactsApiService(service: ContactsApiService) {
        this.apiService = service
    }
    
    fun setUserId(id: Int) {
        this.userId = id
    }
    
    fun setOnContactAddedListener(listener: (Boolean) -> Unit) {
        onContactAddedListener = listener
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}