package com.example.mrsu

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mrsu.network.service.OkHttpApiService
import kotlinx.coroutines.launch
import com.example.mrsu.storage.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginUser = findViewById<EditText>(R.id.user_login)
        val passwordUser = findViewById<EditText>(R.id.user_password)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val username = loginUser.text.toString()
            val password = passwordUser.text.toString()


            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
                    Toast.makeText(this@LoginActivity, "Вход выполнен успешно!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginActivity, ScheduleActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Ошибка входа: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}