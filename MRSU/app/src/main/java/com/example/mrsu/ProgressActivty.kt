package com.example.mrsu

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mrsu.network.model.DisciplineRatingPlanResponse
import com.example.mrsu.network.model.SemesterDiscipline
import com.example.mrsu.network.service.OkHttpApiService
import com.example.mrsu.storage.TokenStorage
import com.example.mrsu.ui.ProgressAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgressActivity : AppCompatActivity() {

    private lateinit var progressListView: ListView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var apiService: OkHttpApiService

    private val disciplinesWithRating = mutableListOf<Pair<SemesterDiscipline, DisciplineRatingPlanResponse?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        progressListView = findViewById(R.id.progressListView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        emptyView = findViewById(R.id.emptyView)
        apiService = OkHttpApiService()

        setupBackButton()
        loadProgressData()
    }

    private fun setupBackButton() {
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadProgressData() {
        lifecycleScope.launch {
            try {
                val token = TokenStorage.accessToken
                if (token.isNullOrEmpty()) {
                    showError("Токен не найден")
                    return@launch
                }

                loadingProgressBar.visibility = android.view.View.VISIBLE
                progressListView.visibility = android.view.View.GONE
                emptyView.visibility = android.view.View.GONE

                // 1. Получаем список дисциплин
                val semester = withContext(Dispatchers.IO) {
                    apiService.getStudentSemester("Bearer $token", "current")
                }

                val disciplines = semester.recordBooks.firstOrNull()?.disciplines
                if (disciplines.isNullOrEmpty()) {
                    showEmpty("Нет дисциплин в текущем семестре")
                    return@launch
                }

                // 2. Для каждой дисциплины получаем рейтинг-план
                disciplinesWithRating.clear()

                for (discipline in disciplines) {
                    try {
                        val ratingPlan = withContext(Dispatchers.IO) {
                            apiService.getDisciplineRatingPlan("Bearer $token", discipline.id)
                        }
                        disciplinesWithRating.add(Pair(discipline, ratingPlan))
                    } catch (e: Exception) {
                        // Если не удалось получить рейтинг-план, добавляем без него
                        disciplinesWithRating.add(Pair(discipline, null))
                        android.util.Log.e("ProgressActivity", "Ошибка загрузки для ${discipline.title}", e)
                    }
                }

                displayProgress()

            } catch (e: Exception) {
                showError("Ошибка загрузки: ${e.message}")
                e.printStackTrace()
            } finally {
                loadingProgressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun displayProgress() {
        if (disciplinesWithRating.isEmpty()) {
            showEmpty("Нет данных об успеваемости")
            return
        }

        progressListView.visibility = android.view.View.VISIBLE
        emptyView.visibility = android.view.View.GONE

        val adapter = ProgressAdapter(this, disciplinesWithRating)
        progressListView.adapter = adapter
    }

    private fun showError(message: String) {
        loadingProgressBar.visibility = android.view.View.GONE
        progressListView.visibility = android.view.View.GONE
        emptyView.visibility = android.view.View.VISIBLE
        emptyView.text = message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showEmpty(message: String) {
        loadingProgressBar.visibility = android.view.View.GONE
        progressListView.visibility = android.view.View.GONE
        emptyView.visibility = android.view.View.VISIBLE
        emptyView.text = message
    }
}