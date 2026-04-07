package com.example.mrsu.network.impl

import com.example.mrsu.network.model.*
import retrofit2.http.*

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

    @GET("v1/StudentSemester")
    suspend fun getStudentSemester(
        @Header("Authorization") authorization: String,
        @Query("selector") selector: String = "current"
    ): StudentSemesterResponse

    @GET("v1/StudentRatingPlan/{disciplineId}")
    suspend fun getDisciplineRatingPlan(
        @Header("Authorization") authorization: String,
        @Path("disciplineId") disciplineId: Int
    ): DisciplineRatingPlanResponse
}