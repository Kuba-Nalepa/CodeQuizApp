package com.jakubn.codequizapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(
    val route: String
) {

    @Serializable
    object Auth : Screen("auth_route")
    @Serializable
    object Welcome : Screen("welcome_route")
    @Serializable
    object Registration : Screen("registration_screen")
    @Serializable
    object Login : Screen("login_screen")
    @Serializable
    object Main : Screen("main_route")
    @Serializable
    object Home : Screen("home_screen")
    @Serializable
    object Leaderboard : Screen("leaderboard_screen")
    @Serializable
    object Profile : Screen("profile_screen")
    @Serializable
    object CreateGame : Screen("create_game_screen")
    @Serializable
    object AvailableGameList : Screen("available_game_list_screen")
    @Serializable
    object Lobby : Screen("lobby_screen")
    @Serializable
    object Quiz : Screen("quiz_screen")
    @Serializable
    object GameOver : Screen("game_over_screen")
    @Serializable
    object Settings : Screen("settings_screen")
    @Serializable
    object UserProfile : Screen("user_profile_screen")
    @Serializable
    object Chat : Screen("chat_screen")

}
