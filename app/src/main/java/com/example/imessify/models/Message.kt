package com.example.imessify.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Message(
    val id: Int = 0,
    
    @SerializedName("sender_id")
    val senderId: Int = 0,
    
    @SerializedName("receiver_id")
    val receiverId: Int = 0,
    
    @SerializedName("sender_username")
    val senderUsername: String = "",
    
    @SerializedName("receiver_username")
    val receiverUsername: String = "",
    
    @SerializedName("message")
    val messageText: String = "",
    
    @SerializedName("is_read")
    val isRead: Boolean = false,
    
    @SerializedName("timestamp")
    val timestamp: String = ""
) {
    fun isFromCurrentUser(userId: Int): Boolean {
        return senderId == userId
    }
}

// Response models for messaging API
data class MessagesResponse(
    val success: Boolean,
    val messages: List<Message>? = null,
    val message: String? = null
)

data class SendMessageResponse(
    val success: Boolean,
    val message: String? = null,
    @SerializedName("receiver_id")
    val receiverId: Int? = null
)

data class SendMessageRequest(
    @SerializedName("sender_id")
    val senderId: Int,
    
    @SerializedName("receiver_name")
    val receiverName: String,
    
    val message: String
)