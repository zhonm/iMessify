package com.example.imessify.api

import com.example.imessify.models.Message
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Login endpoint
    @POST("login.php")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    // Registration endpoint
    @POST("register.php")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    // Get user conversation list
    @GET("conversations.php")
    fun getConversations(@Query("user_id") userId: Int): Call<ConversationsResponse>
    
    // Get messages between two users
    @GET("messages.php")
    suspend fun getMessages(
        @Query("user_id") userId: Int,
        @Query("contact_id") contactId: Int
    ): MessagesResponse
    
    // Send a new message
    @POST("messages.php")
    suspend fun sendMessage(
        @Query("sender_id") senderId: Int,
        @Query("receiver_name") receiverName: String,
        @Query("message") messageText: String
    ): MessageActionResponse
    
    // Find user by username (needed for starting conversations with new users)
    @GET("users.php")
    suspend fun findUserByUsername(@Query("username") username: String): UserResponse
}

data class LoginRequest(
    val email: String,
    val password: String,
    val username: String? = null
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userId: Int? = null,
    val username: String? = null
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("user_id") val userId: Int? = null
)

data class ConversationsResponse(
    val success: Boolean,
    val message: String? = null,
    val conversations: List<Conversation>? = null
)

data class Conversation(
    val id: Int,
    @SerializedName("other_user_id") val otherUserId: Int,
    @SerializedName("other_username") val otherUsername: String,
    @SerializedName("last_message") val lastMessage: String? = null,
    @SerializedName("last_message_timestamp") val lastMessageTimestamp: String? = null,
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("last_updated") val lastUpdated: String = "",
    @SerializedName("profile_image") val profileImage: String? = null
)

data class MessagesResponse(
    val success: Boolean,
    val message: String? = null,
    val messages: List<Message>? = null
)

data class MessageActionResponse(
    val success: Boolean,
    val message: String? = null,
    @SerializedName("receiver_id") val receiverId: Int? = null
)

data class UserResponse(
    val success: Boolean,
    val message: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    val username: String? = null,
    val email: String? = null,
    @SerializedName("profile_image") val profileImage: String? = null
)