package com.example.mrsu


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mrsu.network.service.OkHttpApiService
import com.example.mrsu.storage.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var profilePhoto: ImageView
    private lateinit var userName: TextView
    private lateinit var fioText: TextView
    private lateinit var birthDateText: TextView
    private lateinit var emailText: TextView
    private lateinit var studentCodText: TextView
    private lateinit var logoutButton: Button

    private val apiService = OkHttpApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        initViews()
        setupClickListeners()
        loadProfileData()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        profilePhoto = findViewById(R.id.profilePhoto)
        userName = findViewById(R.id.userName)
        fioText = findViewById(R.id.fioText)
        birthDateText = findViewById(R.id.birthDateText)
        emailText = findViewById(R.id.emailText)
        studentCodText = findViewById(R.id.studentCodText)
        logoutButton = findViewById(R.id.logoutButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            // Возвращаемся на расписание
            val intent = Intent(this, ScheduleActivity::class.java)
            startActivity(intent)
            finish()
        }

        logoutButton.setOnClickListener {
            // Очищаем токен
            TokenStorage.accessToken = null

            // Переходим на экран входа
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfileData() {
        lifecycleScope.launch {
            try {
                val token = TokenStorage.accessToken
                if (token.isNullOrEmpty()) {
                    Toast.makeText(this@ProfileActivity, "Токен не найден", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val profile = withContext(Dispatchers.IO) {
                    apiService.getProfile("Bearer $token")
                }

                displayProfile(profile)

            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Ошибка загрузки профиля: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun displayProfile(profile: com.example.mrsu.network.model.ProfileResponse) {
        userName.text = profile.userName
        fioText.text = profile.fio

        // Форматируем дату рождения
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
            val date = inputFormat.parse(profile.birthDate)
            birthDateText.text = outputFormat.format(date)
        } catch (e: Exception) {
            birthDateText.text = profile.birthDate
        }

        emailText.text = profile.email
        studentCodText.text = profile.studentCod

        // Загружаем фото
        if (profile.photo.urlMedium.isNotBlank()) {
            Glide.with(this)
                .load(profile.photo.urlMedium)
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(profilePhoto)
        }
    }
}