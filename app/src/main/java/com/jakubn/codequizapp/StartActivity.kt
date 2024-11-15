package com.jakubn.codequizapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeQuizAppTheme {
                // In future here will be module for fetching data
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}