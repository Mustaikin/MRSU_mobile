package com.example.mrsu.network.impl

import com.example.mrsu.network.model.AuthResponse
import com.example.mrsu.network.model.ProfileResponse
import com.example.mrsu.network.model.ScheduleResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST// описывает возможности API
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("OAuth/Token")
    suspend fun login(
        @Field("grant_type") grantType: String = "password",
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): AuthResponse

    @GET("v1/StudentTimeTable")
    suspend fun getSchedule(
        @Header("Authorization") authorization: String,
        @Query("date") date: String
    ): List<ScheduleResponse>

    @GET("v1/User")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): ProfileResponse
}

