package com.example.imessify.api

import com.example.imessify.models.Contact
import com.example.imessify.models.ContactRequest
import com.example.imessify.models.User
import com.example.imessify.models.ApiResponse
import com.example.imessify.models.ContactsResponse
import com.example.imessify.models.SearchUsersResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ContactsApiService {
    
    @GET("contacts.php")
    suspend fun getUserContacts(@Query("user_id") userId: Int): ContactsResponse
    
    @POST("contacts.php")
    suspend fun createContact(@Body request: ContactRequest): ApiResponse
    
    @GET("users.php")
    suspend fun searchUsers(@Query("search") query: String, @Query("action") action: String = "search"): SearchUsersResponse
}