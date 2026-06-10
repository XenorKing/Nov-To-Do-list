package com.novproject.todolist.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.novproject.todolist.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        updateFcmToken()
        result.user!!
    }

    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user!!

        val profileUpdates = userProfileChangeRequest { displayName = name }
        user.updateProfile(profileUpdates).await()

        val fcmToken = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()

        firestore.collection("users").document(user.uid).set(
            User(uid = user.uid, displayName = name, email = email, fcmToken = fcmToken)
        ).await()

        user
    }

    suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() = auth.signOut()

    private suspend fun updateFcmToken() {
        val user = auth.currentUser ?: return
        val token = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull() ?: return
        firestore.collection("users").document(user.uid)
            .update("fcmToken", token)
    }
}
