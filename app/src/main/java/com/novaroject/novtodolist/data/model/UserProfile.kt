package com.novaroject.novtodolist.data.model

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val nickname: String = "",
    val email: String = "",
    val fcmToken: String? = null
)
