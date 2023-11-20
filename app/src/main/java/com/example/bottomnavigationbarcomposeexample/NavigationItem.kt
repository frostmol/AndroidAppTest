package com.example.bottomnavigationbarcomposeexample

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Home : NavigationItem("home", R.drawable.ic_home, "Home")
    object Books : NavigationItem("books", R.drawable.ic_book, "Задания")
    object Profile : NavigationItem("profile", R.drawable.ic_profile, "Профиль")
}