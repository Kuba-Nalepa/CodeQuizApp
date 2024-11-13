package com.jakubn.codequizapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Navigate(
    val route: String
) {

    @Serializable
    object Auth : Navigate("auth_route")
    @Serializable
    object Welcome : Navigate("welcome_route")
    @Serializable
    object Registration : Navigate("registration_screen")
    @Serializable
    object Login : Navigate("login_screen")
    @Serializable
    object Main : Navigate("main_route")
    @Serializable
    object Home : Navigate("home_screen")
    @Serializable
    object Category : Navigate("category_screen")
    @Serializable
    object Game : Navigate("navigate_screen")
    @Serializable
    object GameOver : Navigate("game_over_screen")
    @Serializable
    object MyProfile : Navigate("my_profile_screen")
    @Serializable
    object UserProfile : Navigate("user_profile_screen")
    @Serializable
    object Chat : Navigate("chat_screen")

}
