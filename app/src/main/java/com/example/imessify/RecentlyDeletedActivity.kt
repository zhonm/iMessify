package com.example.imessify

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imessify.databinding.RecentlyDeletedBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RecentlyDeletedActivity : AppCompatActivity() {

    private lateinit var binding: RecentlyDeletedBinding
    private lateinit var deletedMessagesAdapter: MessagesAdapter
    private var selectedMessages = ArrayList<UserAdapter>()
    private var isSelectionMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecentlyDeletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadDeletedMessages()
    }

    private fun setupUI() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Empty trash button
        binding.btnEmptyTrash.setOnClickListener {
            showEmptyTrashConfirmationDialog()
        }

        // Setup RecyclerView
        binding.deletedMessagesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Setup selection mode buttons
        binding.btnRestore.setOnClickListener {
            // Handle restore selected messages
            toggleSelectionMode(false)
            showEmptyState(true)
        }

        binding.btnDelete.setOnClickListener {
            // Handle permanent deletion
            toggleSelectionMode(false)
            showEmptyState(true)
        }
    }

    private fun loadDeletedMessages() {
        val deletedMessages = getExampleDeletedMessages()

        if (deletedMessages.isEmpty()) {
            showEmptyState(true)
        } else {
            showEmptyState(false)
            deletedMessagesAdapter = MessagesAdapter(this, deletedMessages)
            binding.deletedMessagesRecyclerView.adapter = deletedMessagesAdapter

            // Add long click listener for selection mode
            deletedMessagesAdapter.setOnItemLongClickListener {
                toggleSelectionMode(true)
            }
        }
    }

    private fun getExampleDeletedMessages(): ArrayList<UserAdapter> {
        val messages = ArrayList<UserAdapter>()

        val imageIds = intArrayOf(
            R.drawable.pic3, R.drawable.pic7, R.drawable.pic9
        )

        val names = arrayOf(
            "Deleted Contact 1",
            "Old Group Chat",
            "Former Colleague"
        )

        val lastMessages = arrayOf(
            "This chat was deleted yesterday",
            "This group was archived last week",
            "Conversation from previous job"
        )

        // Create dates from 5-20 days ago
        val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_MONTH, -5)
        val date1 = formatter.format(calendar.time)

        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val date2 = formatter.format(calendar.time)

        calendar.add(Calendar.DAY_OF_MONTH, -8)
        val date3 = formatter.format(calendar.time)

        val dates = arrayOf(date1, date2, date3)

        val phoneNumbers = arrayOf(
            "09876543210", "Group", "09123456789"
        )

        for (i in names.indices) {
            messages.add(UserAdapter(names[i], lastMessages[i], dates[i], phoneNumbers[i], imageIds[i]))
        }

        return messages
    }

    private fun showEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.deletedMessagesRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.deletedMessagesRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun toggleSelectionMode(enable: Boolean) {
        isSelectionMode = enable
        binding.selectionModeBar.visibility = if (enable) View.VISIBLE else View.GONE

        if (!enable) {
            selectedMessages.clear()
        }
    }

    private fun showEmptyTrashConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Empty Trash")
            .setMessage("Permanently delete all messages? This action cannot be undone.")
            .setPositiveButton("Empty") { dialog, _ ->
                // Delete all messages
                showEmptyState(true)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}