package com.example.imessify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imessify.R
import com.example.imessify.models.Message
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(
    private val currentUserId: Int,
    private val messages: MutableList<Message> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_OUTGOING = 1
        private const val VIEW_TYPE_INCOMING = 2
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_OUTGOING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_outgoing, parent, false)
                OutgoingMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_incoming, parent, false)
                IncomingMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        
        when (holder) {
            is OutgoingMessageViewHolder -> holder.bind(message)
            is IncomingMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.isFromCurrentUser(currentUserId)) {
            VIEW_TYPE_OUTGOING
        } else {
            VIEW_TYPE_INCOMING
        }
    }

    inner class OutgoingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.outgoingMessageText)
        private val messageTime: TextView = itemView.findViewById(R.id.outgoingMessageTime)
        private val messageStatus: ImageView = itemView.findViewById(R.id.outgoingMessageStatus)

        fun bind(message: Message) {
            messageText.text = message.messageText
            messageTime.text = formatTime(message.timestamp)
            
            // Set read status icon
            messageStatus.visibility = View.VISIBLE
            if (message.isRead) {
                messageStatus.setImageResource(android.R.drawable.ic_menu_view)
            } else {
                messageStatus.setImageResource(android.R.drawable.ic_dialog_info)
            }
        }
    }

    inner class IncomingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.incomingMessageText)
        private val messageTime: TextView = itemView.findViewById(R.id.incomingMessageTime)

        fun bind(message: Message) {
            messageText.text = message.messageText
            messageTime.text = formatTime(message.timestamp)
        }
    }

    private fun formatTime(timestamp: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(timestamp) ?: return timestamp
            
            val calendar = Calendar.getInstance()
            val now = calendar.time
            
            calendar.time = date
            
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            
            return when {
                isSameDay(calendar, today) -> {
                    // Today, show time
                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
                }
                isSameDay(calendar, yesterday) -> {
                    // Yesterday
                    "Yesterday " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
                }
                else -> {
                    // Other days, show date and time
                    SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(date)
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