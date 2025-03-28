package com.example.imessify

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes for API communication
data class RegisterRequest(val username: String, val email: String, val password: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class LoginRequest(
    @SerializedName("username") val email: String, // Maps to "username" field in JSON
    val password: String
)
data class LoginResponse(val success: Boolean, val message: String, val username: String? = null)

// API service interface
interface ApiService {
    @POST("register.php")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("login.php")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse
}