package com.jakubn.codequizapp.navigation

sealed class Navigate(
    val route: String
) {
    object AuthScreen : Navigate("auth_screen")

    object HomeScreen : Navigate("home_screen")

    object CategoryScreen : Navigate("category_screen")

    object GameScreen : Navigate("navigate_screen")

    object GameOverScreen : Navigate("game_over_screen")

    object MyProfileScreen : Navigate("my_profile_screen")

    object UserProfileScreen : Navigate("user_profile_screen")

    object ChatScreen : Navigate("chat_screen")

}
