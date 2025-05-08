package com.example.imessify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imessify.R
import com.example.imessify.api.Conversation
import java.text.SimpleDateFormat
import java.util.*

class ConversationsAdapter(
    private var conversations: MutableList<Conversation> = mutableListOf(),
    private val onConversationClickListener: OnConversationClickListener
) : RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder>() {

    interface OnConversationClickListener {
        fun onConversationClick(conversation: Conversation)
    }

    fun updateConversations(newConversations: List<Conversation>) {
        conversations.clear()
        conversations.addAll(newConversations)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.conversationProfileImage)
        private val username: TextView = itemView.findViewById(R.id.conversationUsername)
        private val lastMessage: TextView = itemView.findViewById(R.id.conversationLastMessage)
        private val time: TextView = itemView.findViewById(R.id.conversationTime)
        private val unreadCount: TextView = itemView.findViewById(R.id.conversationUnreadCount)

        fun bind(conversation: Conversation) {
            username.text = conversation.otherUsername
            lastMessage.text = conversation.lastMessage
            time.text = formatTime(conversation.lastUpdated)

            // Set unread count
            if (conversation.unreadCount > 0) {
                unreadCount.visibility = View.VISIBLE
                unreadCount.text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString()
            } else {
                unreadCount.visibility = View.GONE
            }

            // Set click listener
            itemView.setOnClickListener {
                onConversationClickListener.onConversationClick(conversation)
            }

            // Load profile image if available
            if (conversation.profileImage != null && conversation.profileImage.isNotEmpty()) {
                // Here you would use an image loading library like Glide or Picasso
                // For now, we'll use a placeholder
                profileImage.setImageResource(android.R.drawable.ic_menu_gallery)
            } else {
                profileImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }

        private fun formatTime(timestamp: String): String {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(timestamp) ?: return timestamp
                
                val calendar = Calendar.getInstance()
                calendar.time = date
                
                val today = Calendar.getInstance()
                val yesterday = Calendar.getInstance()
                yesterday.add(Calendar.DATE, -1)
                
                return when {
                    isSameDay(calendar, today) -> {
                        // Today, show time only
                        SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
                    }
                    isSameDay(calendar, yesterday) -> {
                        // Yesterday
                        "Yesterday"
                    }
                    calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                        // This year, show month and day
                        SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
                    }
                    else -> {
                        // Different year, show date with year
                        SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(date)
                    }
                }
            } catch (e: Exception) {
                return timestamp
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }
    }
}