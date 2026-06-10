package com.novaroject.novtodolist.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.novaroject.novtodolist.data.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> = runCatching {
        val r = auth.signInWithEmailAndPassword(email, password).await()
        runCatching { refreshFcmToken(r.user!!.uid) }
        r.user!!
    }

    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser> = runCatching {
        // 1. Create Firebase Auth account
        val r = auth.createUserWithEmailAndPassword(email, password).await()
        val user = r.user!!

        // 2. Update display name
        runCatching {
            user.updateProfile(userProfileChangeRequest { displayName = name }).await()
        }

        // 3. Save profile to Firestore (non-critical — silently ignored if Firestore rules deny)
        runCatching {
            val token = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()
            db.collection("users").document(user.uid)
                .set(UserProfile(uid = user.uid, displayName = name, email = email, fcmToken = token))
                .await()
        }

        user
    }

    suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() = auth.signOut()

    private suspend fun refreshFcmToken(uid: String) {
        val token = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull() ?: return
        runCatching { db.collection("users").document(uid).update("fcmToken", token).await() }
    }
}
