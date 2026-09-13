package com.example.okdriverpanicbutton.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object MainSOS : Screen("main_sos")
    data object RegisterMembers : Screen("register_members")
    data object MainMenu : Screen("main_menu")
    data object ViewMembers : Screen("view_members")
    data object History : Screen("history")
    data object EditMessage : Screen("edit_message")
}
