package com.example.mrsu.network.model

import com.google.gson.annotations.SerializedName

data class DisciplineRatingPlanResponse(
    @SerializedName("Id") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("ControlDots") val controlDots: List<ControlDot>?,
    @SerializedName("TotalPoints") val totalPoints: Float?,
    @SerializedName("MaxPoints") val maxPoints: Float?,
    @SerializedName("RatingPlanPoints") val ratingPlanPoints: Float?
)

data class ControlDot(
    @SerializedName("Id") val id: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("Points") val points: Float?,
    @SerializedName("MaxPoints") val maxPoints: Float?,
    @SerializedName("Type") val type: String?,
    @SerializedName("Date") val date: String?,
    @SerializedName("Report") val report: String?
)