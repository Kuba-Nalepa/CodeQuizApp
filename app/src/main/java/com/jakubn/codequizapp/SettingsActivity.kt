package com.jakubn.codequizapp

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.profile.UserProfileEditScreen

class SettingsActivity: ComponentActivity() {

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val user: User? = intent.parcelable("user")

        setContent {
            CodeQuizAppTheme {
                val navController = rememberNavController()
                if (user == null) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("User data is empty")
                    }
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.UserProfileEdit.route
                    ) {
                        composable(Screen.UserProfileEdit.route) {
                            UserProfileEditScreen(user, navController)
                        }
                    }
                }
            }
        }
    }


}