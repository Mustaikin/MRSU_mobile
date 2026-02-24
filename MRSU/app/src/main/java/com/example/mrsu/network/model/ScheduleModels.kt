package com.example.mrsu.network.model


import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("Group") val group: String,
    @SerializedName("PlanNumber") val planNumber: String,
    @SerializedName("FacultyName") val facultyName: String,
    @SerializedName("TimeTableBlockd") val timeTableBlockd: Int,
    @SerializedName("TimeTable") val timeTable: TimeTable
)

data class TimeTable(
    @SerializedName("Date") val date: String,
    @SerializedName("Lessons") val lessons: List<Lesson>
)

data class Lesson(
    @SerializedName("Number") val number: Int,
    @SerializedName("SubgroupCount") val subgroupCount: Int,
    @SerializedName("Disciplines") val disciplines: List<Discipline>
)

data class Discipline(
    @SerializedName("Id") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("Language") val language: String?,
    @SerializedName("LessonType") val lessonType: Int,
    @SerializedName("Remote") val remote: Boolean,
    @SerializedName("Group") val group: String,
    @SerializedName("SubgroupNumber") val subgroupNumber: Int,
    @SerializedName("Teacher") val teacher: Teacher,
    @SerializedName("Auditorium") val auditorium: Auditorium
)

data class Teacher(
    @SerializedName("Id") val id: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("FIO") val fio: String,
    @SerializedName("Photo") val photo: Photo?
)

data class Photo(
    @SerializedName("UrlSmall") val urlSmall: String,
    @SerializedName("UrlMedium") val urlMedium: String,
    @SerializedName("UrlSource") val urlSource: String
)

data class Auditorium(
    @SerializedName("Id") val id: Int,
    @SerializedName("Number") val number: String,
    @SerializedName("Title") val title: String,
    @SerializedName("CampusId") val campusId: Int,
    @SerializedName("CampusTitle") val campusTitle: String
)