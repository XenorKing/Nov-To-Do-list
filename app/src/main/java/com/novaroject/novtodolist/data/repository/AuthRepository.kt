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
        val r = auth.createUserWithEmailAndPassword(email, password).await()
        val user = r.user!!

        // Обновляем displayName = никнейм
        runCatching {
            user.updateProfile(userProfileChangeRequest { displayName = name }).await()
        }

        // Сохраняем профиль в Firestore
        runCatching {
            val token = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()
            db.collection("users").document(user.uid)
                .set(UserProfile(uid = user.uid, displayName = name, nickname = name, email = email, fcmToken = token))
                .await()
        }

        user
    }

    // Fix #6 — обновление никнейма в профиле
    suspend fun updateDisplayName(newName: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Необходимо войти в аккаунт")
        user.updateProfile(userProfileChangeRequest { displayName = newName }).await()
        // Обновляем в Firestore тоже
        runCatching {
            db.collection("users").document(user.uid)
                .update(mapOf("displayName" to newName, "nickname" to newName))
                .await()
        }
        Unit
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
