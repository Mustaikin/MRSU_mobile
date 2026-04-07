package com.example.mrsu.network.model

import com.google.gson.annotations.SerializedName

data class StudentSemesterResponse(
    @SerializedName("RecordBooks") val recordBooks: List<RecordBook>,
    @SerializedName("UnreadedDisCount") val unreadedDisCount: Int,
    @SerializedName("UnreadedDisMesCount") val unreadedDisMesCount: Int,
    @SerializedName("Year") val year: String,
    @SerializedName("Period") val period: Int
)

data class RecordBook(
    @SerializedName("Cod") val cod: String,
    @SerializedName("Number") val number: String,
    @SerializedName("Faculty") val faculty: String,
    @SerializedName("Disciplines") val disciplines: List<SemesterDiscipline>
)

data class SemesterDiscipline(
    @SerializedName("Id") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("Faculty") val faculty: String,
    @SerializedName("PeriodString") val periodString: String,
    @SerializedName("PeriodInt") val periodInt: Int,
    @SerializedName("EducationForm") val educationForm: String,
    @SerializedName("EducationLevel") val educationLevel: String,
    @SerializedName("Specialty") val specialty: String,
    @SerializedName("SpecialtyCod") val specialtyCod: String,
    @SerializedName("Profile") val profile: String,
    @SerializedName("Relevance") val relevance: Boolean,
    @SerializedName("Language") val language: String?
)