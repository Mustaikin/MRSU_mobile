package com.example.mrsu.network.model

data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val token_type: String
)