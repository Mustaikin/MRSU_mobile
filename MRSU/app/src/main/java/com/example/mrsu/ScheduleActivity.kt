package com.example.mrsu

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mrsu.network.model.ScheduleResponse
import com.example.mrsu.network.service.OkHttpApiService
import com.example.mrsu.storage.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.view.View
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.content.res.ColorStateList


class ScheduleActivity : AppCompatActivity() {

    private lateinit var daysContainer: LinearLayout
    private lateinit var selectedDateText: TextView
    private lateinit var scheduleListView: ListView
    private lateinit var subgroup1Button: Button
    private lateinit var subgroup2Button: Button

    private val apiService = OkHttpApiService()
    private val weekFormat = SimpleDateFormat("EEE", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    private val fullDateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Все возможные пары с 1 по 8
    private val lessonTimes = mapOf(
        1 to "08:00 - 09:30",
        2 to "09:45 - 11:15",
        3 to "11:35 - 13:05",
        4 to "13:20 - 14:50",
        5 to "15:00 - 16:30",
        6 to "16:40 - 18:10",
        7 to "18:15 - 19:45",
        8 to "19:50 - 21:20"
    )

    private lateinit var currentWeekStart: Calendar
    private lateinit var selectedCalendar: Calendar
    private var currentSubgroup: Int = 0 // 0 - все подгруппы, 1 - подгруппа 1, 2 - подгруппа 2
    private var scheduleData: List<ScheduleResponse> = listOf()
    private lateinit var profileButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        daysContainer = findViewById(R.id.daysContainer)
        selectedDateText = findViewById(R.id.selectedDateText)
        scheduleListView = findViewById(R.id.scheduleListView)
        subgroup1Button = findViewById(R.id.subgroup1Button)
        subgroup2Button = findViewById(R.id.subgroup2Button)

        setupSubgroupButtons()
        setupCalendar()
        loadScheduleForDate(Date())

        profileButton = findViewById(R.id.profileButton)

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupSubgroupButtons() {
        // По умолчанию выбрана подгруппа 1
        updateSubgroupButtonSelection(1)

        subgroup1Button.setOnClickListener {
            currentSubgroup = 1
            updateSubgroupButtonSelection(1)
            displayFilteredSchedule()
        }

        subgroup2Button.setOnClickListener {
            currentSubgroup = 2
            updateSubgroupButtonSelection(2)
            displayFilteredSchedule()
        }
    }

    private fun updateSubgroupButtonSelection(selected: Int) {
        // Сбрасываем стили
        subgroup1Button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F0FE"))
        subgroup1Button.setTextColor(Color.parseColor("#6200EE"))
        subgroup2Button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F0FE"))
        subgroup2Button.setTextColor(Color.parseColor("#6200EE"))

        // Выделяем выбранную
        if (selected == 1) {
            subgroup1Button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6200EE"))
            subgroup1Button.setTextColor(Color.WHITE)
        } else {
            subgroup2Button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6200EE"))
            subgroup2Button.setTextColor(Color.WHITE)
        }
    }

    private fun setupCalendar() {
        currentWeekStart = Calendar.getInstance()
        selectedCalendar = Calendar.getInstance()

        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        displayWeek()
    }

    private fun displayWeek() {
        daysContainer.removeAllViews()

        val calendar = currentWeekStart.clone() as Calendar

        addNavigationButton("◀", -1)

        var foundSelectedDay = false

        for (i in 0 until 7) {
            val dayView = layoutInflater.inflate(R.layout.item_day, daysContainer, false)

            val dayWeek = dayView.findViewById<TextView>(R.id.dayWeek)
            val dayNumber = dayView.findViewById<TextView>(R.id.dayNumber)
            val dayMonth = dayView.findViewById<TextView>(R.id.dayMonth)
            val dayLayout = dayView.findViewById<View>(R.id.dayLayout)

            val date = calendar.time
            dayWeek.text = weekFormat.format(date).uppercase()
            dayNumber.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
            dayMonth.text = monthFormat.format(date)

            val today = Calendar.getInstance()
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                val background = GradientDrawable()
                background.shape = GradientDrawable.RECTANGLE
                background.cornerRadius = 12f
                background.setColor(Color.parseColor("#E8F0FE"))
                background.setStroke(2, Color.parseColor("#6200EE"))
                dayLayout.background = background
            }

            if (!foundSelectedDay &&
                calendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)) {
                dayLayout.isSelected = true
                selectedDateText.text = fullDateFormat.format(date)
                foundSelectedDay = true
            }

            val selectedDate = date
            val currentDayCalendar = calendar.clone() as Calendar

            dayView.setOnClickListener {
                for (j in 0 until daysContainer.childCount) {
                    try {
                        val childLayout = daysContainer.getChildAt(j).findViewById<View>(R.id.dayLayout)
                        childLayout.isSelected = false
                    } catch (e: Exception) {
                        // Пропускаем кнопки навигации
                    }
                }
                dayLayout.isSelected = true
                selectedCalendar = currentDayCalendar
                selectedDateText.text = fullDateFormat.format(selectedDate)
                loadScheduleForDate(selectedDate)
            }

            daysContainer.addView(dayView)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        if (!foundSelectedDay && daysContainer.childCount > 1) {
            try {
                val firstDayLayout = daysContainer.getChildAt(1).findViewById<View>(R.id.dayLayout)
                firstDayLayout.isSelected = true
                val firstDate = (currentWeekStart.clone() as Calendar).time
                selectedDateText.text = fullDateFormat.format(firstDate)
            } catch (e: Exception) {
                // Игнорируем
            }
        }

        addNavigationButton("▶", 1)
    }

    private fun addNavigationButton(text: String, weekOffset: Int) {
        val navButton = layoutInflater.inflate(R.layout.item_nav_button, daysContainer, false)
        val button = navButton.findViewById<TextView>(R.id.navButton)
        button.text = text
        button.setOnClickListener {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, weekOffset)
            displayWeek()
        }
        daysContainer.addView(navButton)
    }

    private fun loadScheduleForDate(date: Date) {
        val dateStr = apiDateFormat.format(date)

        lifecycleScope.launch {
            try {
                val token = TokenStorage.accessToken
                if (token.isNullOrEmpty()) {
                    Toast.makeText(this@ScheduleActivity, "Токен не найден", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val response = withContext(Dispatchers.IO) {
                    apiService.getSchedule("Bearer $token", dateStr)
                }

                scheduleData = response
                displayFilteredSchedule()

            } catch (e: Exception) {
                Toast.makeText(this@ScheduleActivity, "Ошибка загрузки: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
                displayTestSchedule()
            }
        }
    }

    private fun displayFilteredSchedule() {
        if (scheduleData.isEmpty()) {
            displayTestSchedule()
            return
        }

        val lessonsByNumber = mutableMapOf<Int, MutableList<Map<String, String>>>()

        for (i in 1..8) {
            lessonsByNumber[i] = mutableListOf()
        }

        for (schedule in scheduleData) {
            for (lesson in schedule.timeTable.lessons) {
                val lessonNumber = lesson.number
                if (lessonNumber in 1..8) {
                    for (discipline in lesson.disciplines) {
                        // Фильтруем по подгруппе
                        if (currentSubgroup == 0 || discipline.subgroupNumber == 0 || discipline.subgroupNumber == currentSubgroup) {
                            val time = lessonTimes[lessonNumber] ?: "??:?? - ??:??"
                            val teacherName = formatTeacherName(discipline.teacher.fio)

                            var details = "$teacherName • ауд. ${discipline.auditorium.number}"
                            if (discipline.auditorium.campusTitle.isNotBlank()) {
                                details += " (${discipline.auditorium.campusTitle})"
                            }

                            val lessonMap = mutableMapOf(
                                "time" to time,
                                "name" to discipline.title,
                                "details" to details,
                                "number" to lessonNumber.toString()
                            )

                            if (discipline.subgroupNumber > 0) {
                                lessonMap["subgroup"] = "Подгруппа ${discipline.subgroupNumber}"
                            } else {
                                lessonMap["subgroup"] = ""
                            }

                            lessonsByNumber[lessonNumber]?.add(lessonMap)
                        }
                    }
                }
            }
        }

        val finalLessonsList = ArrayList<Map<String, String>>()

        for (i in 1..8) {
            val lessonsForNumber = lessonsByNumber[i] ?: emptyList()

            if (lessonsForNumber.isEmpty()) {
                finalLessonsList.add(mapOf(
                    "time" to (lessonTimes[i] ?: "??:?? - ??:??"),
                    "name" to "",
                    "details" to "",
                    "subgroup" to ""
                ))
            } else {
                finalLessonsList.addAll(lessonsForNumber)
            }
        }

        val adapter = SimpleAdapter(
            this,
            finalLessonsList,
            R.layout.item_schedule,
            arrayOf("time", "name", "details", "subgroup"),
            intArrayOf(R.id.lessonTime, R.id.lessonName, R.id.lessonDetails, R.id.lessonSubgroup)
        )

        scheduleListView.adapter = adapter
    }

    private fun displayTestSchedule() {
        val testList = ArrayList<Map<String, String>>()

        testList.add(mapOf(
            "time" to "08:00 - 09:30",
            "name" to "",
            "details" to "",
            "subgroup" to ""
        ))

        testList.add(mapOf(
            "time" to "09:40 - 11:10",
            "name" to "",
            "details" to "",
            "subgroup" to ""
        ))

        testList.add(mapOf(
            "time" to "11:20 - 12:50",
            "name" to "Дополнительные главы теории вероятностей",
            "details" to "Куляшова Н.М. • ауд. 704 (29)",
            "subgroup" to "Подгруппа 1"
        ))

        testList.add(mapOf(
            "time" to "13:15 - 14:45",
            "name" to "Дополнительные главы теории вероятностей",
            "details" to "Куляшова Н.М. • ауд. 704 (29)",
            "subgroup" to "Подгруппа 1"
        ))

        testList.add(mapOf(
            "time" to "14:55 - 16:25",
            "name" to "Элементы теории принятия решений",
            "details" to "Мурюмин С.М. • ауд. 301 (1)",
            "subgroup" to ""
        ))

        testList.add(mapOf(
            "time" to "16:35 - 18:05",
            "name" to "Элементы теории принятия решений",
            "details" to "Мурюмин С.М. • ауд. 301 (1)",
            "subgroup" to ""
        ))

        testList.add(mapOf(
            "time" to "18:15 - 19:45",
            "name" to "",
            "details" to "",
            "subgroup" to ""
        ))

        testList.add(mapOf(
            "time" to "19:55 - 21:25",
            "name" to "",
            "details" to "",
            "subgroup" to ""
        ))

        val adapter = SimpleAdapter(
            this,
            testList,
            R.layout.item_schedule,
            arrayOf("time", "name", "details", "subgroup"),
            intArrayOf(R.id.lessonTime, R.id.lessonName, R.id.lessonDetails, R.id.lessonSubgroup)
        )

        scheduleListView.adapter = adapter
    }

    private fun formatTeacherName(fullName: String): String {
        val parts = fullName.split(" ")
        return when (parts.size) {
            1 -> fullName
            2 -> "${parts[0]} ${parts[1].first()}."
            3 -> "${parts[0]} ${parts[1].first()}.${parts[2].first()}."
            else -> fullName
        }
    }
}