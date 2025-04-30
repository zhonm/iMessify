package com.example.imessify

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

// Data classes for user authentication
data class RegisterRequest(val username: String, val email: String, val password: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class LoginRequest(
    @SerializedName("username") val email: String, // Maps to "username" field in JSON
    val password: String
)
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val username: String? = null,
    val userId: Int? = null
)

// Data classes for contact operations
data class Contact(
    val id: Int? = null,
    val name: String,
    val phone: String,
    val userId: Int,
    @SerializedName("image_url") val imageUrl: String? = null
)

data class ContactResponse(
    val success: Boolean,
    val message: String,
    val contacts: List<Contact>? = null
)

data class ContactRequest(
    val userId: Int,
    val name: String,
    val phone: String,
    val imageUrl: String? = null
)

data class ContactActionResponse(
    val success: Boolean,
    val message: String,
    val contactId: Int? = null
)

// API service interface
interface ApiService {
    // User authentication endpoints
    @POST("register.php")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("login.php")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    // Contact management endpoints
    @GET("contacts.php")
    suspend fun getContacts(@Query("user_id") userId: Int): ContactResponse

    @POST("contacts.php")
    suspend fun createContact(@Body contact: ContactRequest): ContactActionResponse

    @PUT("contacts.php")
    suspend fun updateContact(@Query("id") contactId: Int, @Body contact: ContactRequest): ContactActionResponse

    @DELETE("contacts.php")
    suspend fun deleteContact(@Query("id") contactId: Int): ContactActionResponse
}