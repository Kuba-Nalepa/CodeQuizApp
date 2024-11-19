package com.jakubn.codequizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.room.Room
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.room.answer.AnswersDatabase
import com.jakubn.codequizapp.room.answer.AnswersViewModel
import com.jakubn.codequizapp.room.question.QuestionsDatabase
import com.jakubn.codequizapp.room.question.QuestionsViewModel
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.authorization.LoginScreen
import com.jakubn.codequizapp.ui.authorization.RegistrationScreen
import com.jakubn.codequizapp.ui.authorization.WelcomeScreen
import com.jakubn.codequizapp.ui.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val questionsDb by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = QuestionsDatabase::class.java,
            name = "questions.db"
        ).build()
    }
    private val answersDb by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = AnswersDatabase::class.java,
            name = "answers.db"
        ).build()
    }
    private val questionsViewModel by viewModels<QuestionsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QuestionsViewModel(questionsDb.dao) as T
                }
            }
        }
    )
    private val answersViewModel by viewModels<AnswersViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AnswersViewModel(answersDb.dao) as T
                }
            }
        }
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()

            CodeQuizAppTheme {
                NavHost(
                    navController = navHostController,
                    startDestination = Screen.Auth.route
                ) {
                    navigation(
                        route = Screen.Auth.route,
                        startDestination = Screen.Welcome.route
                    ) {
                        composable(route = Screen.Welcome.route) {
                            WelcomeScreen(navHostController)
                        }
                        composable(route = Screen.Registration.route) {
                            RegistrationScreen()
                        }
                        composable(route = Screen.Login.route) {
                            LoginScreen(navHostController)
                        }
                    }

                    navigation(
                        route = Screen.Main.route,
                        startDestination = Screen.Home.route
                    ) {
                        composable(route = Screen.Home.route) {
                            MainScreen(navHostController) {
                                HomeScreen(navHostController)
                            }
                        }
                        composable(route = Screen.Leaderboard.route) {
                            MainScreen(navHostController) {

                            }
                        }
                        composable(route = Screen.MyProfile.route) {
                            MainScreen(navHostController) {

                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CodeQuizAppTheme {

    }
}