package com.example.mrsu.network.model


import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("Email") val email: String,
    @SerializedName("EmailConfirmed") val emailConfirmed: Boolean,
    @SerializedName("EnglishFIO") val englishFIO: String,
    @SerializedName("TeacherCod") val teacherCod: String?,
    @SerializedName("StudentCod") val studentCod: String,
    @SerializedName("BirthDate") val birthDate: String,
    @SerializedName("AcademicDegree") val academicDegree: String?,
    @SerializedName("AcademicRank") val academicRank: String?,
    @SerializedName("Roles") val roles: List<Role>,
    @SerializedName("Id") val id: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("FIO") val fio: String,
    @SerializedName("Photo") val photo: Photo
)

data class Role(
    @SerializedName("Name") val name: String,
    @SerializedName("Description") val description: String?
)