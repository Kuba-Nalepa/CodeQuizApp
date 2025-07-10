package com.jakubn.codequizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.navigation.AuthNavGraph
import com.jakubn.codequizapp.navigation.MainNavGraph
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val currentUser by viewModel.currentUser.collectAsState()
            val navController = rememberNavController()
            val context = LocalContext.current

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
        }
    }
}