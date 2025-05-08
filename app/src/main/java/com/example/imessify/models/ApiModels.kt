package com.example.imessify.models

import com.google.gson.annotations.SerializedName

// Login Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    val username: String? = null
)

// Registration Models
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String? = null,
    @SerializedName("user_id")
    val userId: Int? = null
)

// Conversation Models
data class Conversation(
    val id: Int,
    @SerializedName("other_user_id")
    val otherUserId: Int,
    @SerializedName("username")
    val otherUsername: String,
    @SerializedName("last_message")
    val lastMessage: String,
    @SerializedName("unread_count")
    val unreadCount: Int,
    @SerializedName("last_updated")
    val lastUpdated: String,
    @SerializedName("profile_image")
    val profileImage: String? = null
)

data class ConversationsResponse(
    val success: Boolean,
    val conversations: List<Conversation>? = null,
    val message: String? = null
)

// Profile Models
data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("profile_image")
    val profileImage: String? = null,
    @SerializedName("created_at")
    val createdAt: String
)

data class ProfileResponse(
    val success: Boolean,
    val profile: UserProfile? = null,
    val message: String? = null
)

// User Models
data class User(
    val id: Int,
    val username: String,
    val email: String? = null,
    @SerializedName("profile_image")
    val profileImage: String? = null
)

data class UserSearchResponse(
    val success: Boolean,
    val users: List<User>? = null,
    val message: String? = null
)

// Contact Models
data class Contact(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val username: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class ContactsResponse(
    val success: Boolean,
    val message: String? = null,
    val contacts: List<Contact>? = null
)

data class ContactRequest(
    val userId: Int,
    val name: String,
    val username: String,
    val imageUrl: String? = null
)

data class SearchUsersResponse(
    val success: Boolean,
    val message: String? = null,
    val users: List<User>? = null
)

data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)