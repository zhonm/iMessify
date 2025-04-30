package com.example.imessify

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imessify.databinding.FragmentContactsBinding
import com.example.imessify.databinding.DialogAddContactBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactsAdapter: ContactsAdapter
    private var contactsList = ArrayList<ContactModel>() // Changed from UserAdapter to ContactModel
    private var userId: Int = -1

    private val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://imessify.x10.mx/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user ID from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(context, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
            redirectToLogin()
            return
        }

        // Setup navigation drawer toggle
        binding.btnMenu.setOnClickListener {
            (activity as MainActivity).toggleDrawer()
        }
        
        // Set up the add contact button click listener
        binding.btnAddContact.setOnClickListener {
            showAddContactDialog()
        }

        // Setup back button listener to show logout dialog
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showLogoutConfirmationDialog()
                }
            }
        )

        setupRecyclerView()
        loadContacts()
        setupSearch()
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter(requireContext())
        contactsAdapter.setOnItemLongClickListener { position, contact ->
            showContactActionDialog(position, contact)
        }

        binding.contactsRecyclerView.apply {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            setItemViewCacheSize(20)
        }
    }

    private fun loadContacts() {
        showLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getContacts(userId)

                withContext(Dispatchers.Main) {
                    if (response.success && response.contacts != null) {
                        // Convert API response to ContactModel objects (changed from UserAdapter)
                        contactsList = ArrayList(response.contacts.map { contact ->
                            ContactModel(
                                name = contact.name,
                                last_Message = "",
                                last_Msg_time = "",
                                phone_no = contact.phone,
                                image_id = R.drawable.pic1, // Default image for now
                                id = contact.id ?: -1
                            )
                        })

                        contactsAdapter.submitList(contactsList)
                    } else {
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    }
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e("ContactsFragment", "Error loading contacts", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Implement loading indicator if needed
    }

    private fun setupSearch() {
        binding.searchContacts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterContacts(s.toString())
            }
        })
    }

    private fun filterContacts(query: String) {
        if (query.isEmpty()) {
            contactsAdapter.submitList(contactsList)
        } else {
            val filteredList = contactsList.filter {
                it.name.lowercase().contains(query.lowercase()) ||
                        it.phone_no.contains(query)
            }
            contactsAdapter.submitList(filteredList)
        }
    }

    private fun showAddContactDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogBinding = DialogAddContactBinding.inflate(inflater)

        builder.setView(dialogBinding.root)
            .setTitle("Add New Contact")
            .setPositiveButton("Add") { dialog, _ ->
                val name = dialogBinding.etContactName.text.toString().trim()
                val phone = dialogBinding.etContactPhone.text.toString().trim()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    addContact(name, phone)
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showEditContactDialog(contact: ContactModel) { // Changed parameter type
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogBinding = DialogAddContactBinding.inflate(inflater)

        // Pre-fill with existing data
        dialogBinding.etContactName.setText(contact.name)
        dialogBinding.etContactPhone.setText(contact.phone_no)

        builder.setView(dialogBinding.root)
            .setTitle("Edit Contact")
            .setPositiveButton("Update") { dialog, _ ->
                val name = dialogBinding.etContactName.text.toString().trim()
                val phone = dialogBinding.etContactPhone.text.toString().trim()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    updateContact(contact.id, name, phone)
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showContactActionDialog(position: Int, contact: ContactModel) { // Changed parameter type
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(requireContext())
            .setTitle("Contact Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditContactDialog(contact)
                    1 -> showDeleteConfirmationDialog(contact)
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(contact: ContactModel) { // Changed parameter type
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteContact(contact.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addContact(name: String, phone: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = ContactRequest(
                    userId = userId,
                    name = name,
                    phone = phone,
                    imageUrl = null // Using default image for now
                )

                val response = apiService.createContact(request)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show()
                        // Reload contacts to get the updated list with IDs
                        loadContacts()
                    } else {
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsFragment", "Error adding contact", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateContact(id: Int, name: String, phone: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = ContactRequest(
                    userId = userId,
                    name = name,
                    phone = phone,
                    imageUrl = null // Using default image for now
                )

                val response = apiService.updateContact(id, request)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        Toast.makeText(context, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                        loadContacts()
                    } else {
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsFragment", "Error updating contact", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteContact(id: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteContact(id)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        Toast.makeText(context, "Contact deleted successfully", Toast.LENGTH_SHORT).show()
                        loadContacts()
                    } else {
                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsFragment", "Error deleting contact", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        // Existing implementation
    }

    private fun logoutUser() {
        // Clear user data
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
