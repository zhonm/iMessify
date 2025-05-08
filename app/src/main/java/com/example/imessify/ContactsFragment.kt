package com.example.imessify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imessify.adapters.ContactsAdapter
import com.example.imessify.adapters.UserSearchAdapter
import com.example.imessify.api.ContactsApiService
import com.example.imessify.databinding.FragmentContactsBinding
import com.example.imessify.models.Contact
import com.example.imessify.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ContactsFragment : Fragment(), ContactsAdapter.OnContactClickListener {
    private lateinit var binding: FragmentContactsBinding
    private lateinit var adapter: ContactsAdapter
    private var userId: Int = -1
    private var contacts = mutableListOf<Contact>()

    // Retrofit service with corrected base URL
    private val apiService: ContactsApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://imessify.x10.mx/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ContactsApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get userId from SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getInt("user_id", -1)
        
        if (userId == -1) {
            // If user is not logged in, redirect to login
            Toast.makeText(context, "Please log in to access contacts", Toast.LENGTH_SHORT).show()
            // Add logic to navigate to login if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        
        binding.btnAddContact.setOnClickListener {
            showAddContactDialog()
        }
        
        // Show loading indicator
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.emptyContactsText.visibility = View.GONE
        
        // Load contacts when view is created
        loadContacts()
    }
    
    private fun setupRecyclerView() {
        adapter = ContactsAdapter(this)
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.contactsRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.contactsRecyclerView.adapter = adapter
    }
    
    private fun loadContacts() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getUserContacts(userId)
                
                withContext(Dispatchers.Main) {
                    binding.loadingProgressBar.visibility = View.GONE
                    
                    if (response.success && response.contacts != null) {
                        contacts = response.contacts.toMutableList()
                        adapter.submitList(contacts)
                        
                        // Update UI based on contact count
                        if (contacts.isEmpty()) {
                            binding.emptyContactsText.visibility = View.VISIBLE
                            binding.contactsRecyclerView.visibility = View.GONE
                        } else {
                            binding.emptyContactsText.visibility = View.GONE
                            binding.contactsRecyclerView.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(context, response.message ?: "Failed to load contacts", Toast.LENGTH_SHORT).show()
                        binding.emptyContactsText.visibility = View.VISIBLE
                        binding.contactsRecyclerView.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsFragment", "Error loading contacts", e)
                withContext(Dispatchers.Main) {
                    binding.loadingProgressBar.visibility = View.GONE
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.emptyContactsText.visibility = View.VISIBLE
                    binding.contactsRecyclerView.visibility = View.GONE
                }
            }
        }
    }

    private fun showAddContactDialog() {
        // Create and configure the new dialog fragment
        val dialogFragment = AddContactDialogFragment().apply {
            setUserId(this@ContactsFragment.userId)
            setContactsApiService(this@ContactsFragment.apiService)
            setOnContactAddedListener { success ->
                if (success) {
                    // Reload contacts after successful addition
                    loadContacts()
                }
            }
        }
        
        // Show the dialog
        dialogFragment.show(childFragmentManager, "AddContactDialog")
    }
    
    // Override method for contact click handling
    override fun onContactClick(contact: Contact) {
        val intent = Intent(requireContext(), MessageChatActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
            putExtra("CONTACT_NAME", contact.name)
        }
        startActivity(intent)
    }
}
