package com.novproject.todolist.data.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val fcmToken: String? = null
)
