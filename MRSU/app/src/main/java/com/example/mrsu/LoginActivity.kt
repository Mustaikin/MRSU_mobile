package com.example.mrsu

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mrsu.network.service.OkHttpApiService
import com.example.mrsu.storage.TokenStorage
import com.example.mrsu.storage.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var loginUser: EditText
    private lateinit var passwordUser: EditText
    private lateinit var loginButton: Button
    private lateinit var saveLoginCheckBox: CheckBox
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userPreferences = UserPreferences(this)

        loginUser = findViewById(R.id.user_login)
        passwordUser = findViewById(R.id.user_password)
        loginButton = findViewById(R.id.login_button)
        saveLoginCheckBox = findViewById(R.id.saveLoginCheckBox)

        // Проверяем, сохранен ли вход
        checkSavedLogin()

        loginButton.setOnClickListener {
            val username = loginUser.text.toString()
            val password = passwordUser.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(username, password)
        }
    }

    private fun checkSavedLogin() {
        if (userPreferences.isLoggedIn && userPreferences.saveLogin) {
            // Автоматически заполняем поля
            loginUser.setText(userPreferences.username)
            passwordUser.setText(userPreferences.password)
            saveLoginCheckBox.isChecked = true

            // Показываем сообщение
            Toast.makeText(this, "Выполняется автоматический вход...", Toast.LENGTH_SHORT).show()

            // Выполняем вход
            performLogin(userPreferences.username, userPreferences.password)
        }
    }

    private fun performLogin(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val apiService = OkHttpApiService()

                val response = withContext(Dispatchers.IO) {
                    apiService.login(
                        grantType = "password",
                        username = username,
                        password = password,
                        clientId = "8",
                        clientSecret = "qweasd"
                    )
                }

                TokenStorage.accessToken = response.access_token

                // Сохраняем данные если включен чекбокс
                if (saveLoginCheckBox.isChecked) {
                    userPreferences.apply {
                        this.username = username
                        this.password = password
                        this.isLoggedIn = true
                        this.saveLogin = true
                    }
                } else {
                    // Если чекбокс не отмечен - очищаем сохраненные данные
                    userPreferences.clear()
                }

                Toast.makeText(this@LoginActivity, "Вход выполнен успешно!", Toast.LENGTH_LONG).show()

                val intent = Intent(this@LoginActivity, ScheduleActivity::class.java)
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Ошибка входа: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}