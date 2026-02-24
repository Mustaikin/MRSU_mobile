package com.example.mrsu.network.service

import com.example.mrsu.network.impl.ApiService
import com.example.mrsu.network.model.AuthResponse
import com.example.mrsu.network.model.ProfileResponse
import com.example.mrsu.network.model.ScheduleResponse
import com.example.mrsu.storage.TokenStorage
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.util.concurrent.TimeUnit

class OkHttpApiService : ApiService {

    private val client: OkHttpClient
    private val gson: Gson
    private val baseUrl = "https://p.mrsu.ru"

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    override suspend fun login(
        grantType: String,
        username: String,
        password: String,
        clientId: String,
        clientSecret: String
    ): AuthResponse = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder()
            .add("grant_type", grantType)
            .add("username", username)
            .add("password", password)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url("$baseUrl/OAuth/Token")
            .post(formBody)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    throw IOException("Ошибка HTTP ${response.code}: $errorBody")
                }

                val responseBody = response.body?.string()
                    ?: throw IOException("Пустой ответ от сервера")

                gson.fromJson(responseBody, AuthResponse::class.java)
            }
        } catch (e: Exception) {
            throw IOException("Ошибка сети: ${e.message}", e)
        }
    }

    override suspend fun getSchedule(
        authorization: String,
        date: String
    ): List<ScheduleResponse> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://papi.mrsu.ru/v1/StudentTimeTable?date=$date")
            .get()
            .addHeader("Authorization", authorization)
            .addHeader("Accept", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    throw IOException("Ошибка HTTP ${response.code}: $errorBody")
                }

                val responseBody = response.body?.string()
                    ?: throw IOException("Пустой ответ от сервера")

                val listType = object : TypeToken<List<ScheduleResponse>>() {}.type
                gson.fromJson<List<ScheduleResponse>>(responseBody, listType)
            }
        } catch (e: Exception) {
            throw IOException("Ошибка сети: ${e.message}", e)
        }
    }

    override suspend fun getProfile(authorization: String): ProfileResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://papi.mrsu.ru/v1/User")
            .get()
            .addHeader("Authorization", authorization)
            .addHeader("Accept", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    throw IOException("Ошибка HTTP ${response.code}: $errorBody")
                }

                val responseBody = response.body?.string()
                    ?: throw IOException("Пустой ответ от сервера")

                gson.fromJson(responseBody, ProfileResponse::class.java)
            }
        } catch (e: Exception) {
            throw IOException("Ошибка сети: ${e.message}", e)
        }
    }
}