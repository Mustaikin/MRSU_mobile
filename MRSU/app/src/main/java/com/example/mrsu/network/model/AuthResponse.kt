package com.example.mrsu.network.model

// класс данных для токена.
// Kotlin переделывает json объект с запроса в экземпляр данных
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val token_type: String
)