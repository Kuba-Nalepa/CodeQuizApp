package com.jakubn.codequizapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {

    // --- Main Navigation Graphs ---
    @Serializable
    data object AuthGraph : Screen("auth_graph")
    @Serializable
    data object MainGraph : Screen("main_graph")
    @Serializable
    data object SettingsGraph : Screen("settings_graph")

    // --- Authentication Screens ---
    @Serializable
    data object Welcome : Screen("welcome_screen")
    @Serializable
    data object Registration : Screen("registration_screen")
    @Serializable
    data object Login : Screen("login_screen")


    // --- Main App Screens (Bottom Navigation Destinations) ---
    @Serializable
    data object Home : Screen("home_screen")
    @Serializable
    data object Leaderboard : Screen("leaderboard_screen")
    @Serializable
    data object Settings : Screen("settings_screen")

    // --- Game Related Screens ---
    @Serializable
    data object CreateGame : Screen("create_game_screen")
    @Serializable
    data object AvailableGameList : Screen("available_game_list_screen")
    @Serializable
    data object Lobby : Screen("lobby_screen")
    @Serializable
    data object Quiz : Screen("quiz_screen")
    @Serializable
    data object GameOver : Screen("game_over_screen")
    @Serializable
    data object QuizReviewScreen : Screen("quiz_review_screen")

    // --- Profile/Settings Details Screens ---
    @Serializable
    data object UserProfileEdit : Screen("user_profile_edit_screen")

    // --- Miscellaneous ---
    @Serializable
    data object Chat : Screen("chat_screen?friendUid={friendUid}&friendName={friendName}") {
        // A helper function to build the final route
        fun createRoute(friendUid: String, friendName: String): String {
            return "chat_screen?friendUid=$friendUid&friendName=$friendName"
        }
    }

}