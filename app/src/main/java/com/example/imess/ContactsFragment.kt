package com.example.imess

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imess.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactsAdapter: ContactsAdapter
    private var originalList = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadContacts()
        setupSearch()
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter(requireContext())
        binding.contactsRecyclerView.apply {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            setItemViewCacheSize(20)
        }
    }

    private fun loadContacts() {
        val imageId = intArrayOf(
            R.drawable.pic1, R.drawable.pic11, R.drawable.pic12, R.drawable.pic13, R.drawable.pic10,
            R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9
        )
        val name = arrayOf(
            "Myles the spidey", "Eyables", "Spakol", "Dayaper", "Hayden and Pogi", "Ayu the kups",
            "Jose the aswang", "Hayden the small", "Groc", "Kupalerski",
            "Grabe ba", "Angas naman", "Pogito"
        )
        val phone_no = arrayOf(
            "09123456789", "09123456789", "09123456789", "09123456789", "09123456789",
            "09123456789", "09123456789", "09123456789", "09123456789",
            "09123456789", "09123456789", "09123456789", "09123456789",
        )

        originalList = ArrayList()

        for (i in name.indices) {
            val contact = User(name[i], "", "", phone_no[i], imageId[i])
            originalList.add(contact)
        }

        // Sort contacts alphabetically
        originalList.sortBy { it.name }
        contactsAdapter.submitList(originalList)
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
            contactsAdapter.submitList(originalList)
        } else {
            val filteredList = originalList.filter {
                it.name.lowercase().contains(query.lowercase())
            }
            contactsAdapter.submitList(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}