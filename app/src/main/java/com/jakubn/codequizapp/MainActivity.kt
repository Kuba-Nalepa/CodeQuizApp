package com.jakubn.codequizapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.navigation.AuthNavGraph
import com.jakubn.codequizapp.navigation.MainNavGraph
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            val currentUser by viewModel.currentUser.collectAsState()
            val navController = rememberNavController()
            val context = LocalContext.current


            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }

            CodeQuizAppTheme {
                when (val customStateUser = currentUser) {
                    is CustomState.Success -> {
                        val user = customStateUser.result
                        MainNavGraph(
                            navController = navController,
                            user = user,
                            appContext = context,
                            onLogout = {
                                viewModel.logout()
                            }
                        )
                    }

                    is CustomState.Failure -> {
                        AuthNavGraph(
                            navController = navController,
                            onAuthSuccess = { loggedInUser ->
                                viewModel.onLoginSuccess(loggedInUser)
                            }
                        )
                    }

                    CustomState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    CustomState.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Idle State")
                        }
                    }
                }
            }
            handleIntent(intent,viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent, viewModel)
    }

    private fun handleIntent(intent: Intent?, viewModel: MainViewModel) {
        if (intent?.getStringExtra("notification_type") == "friend_request") {
            viewModel.navigateTo("home_screen")
        }
    }
}