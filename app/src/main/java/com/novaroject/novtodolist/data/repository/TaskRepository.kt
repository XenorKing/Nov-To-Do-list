package com.novaroject.novtodolist.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.novaroject.novtodolist.data.model.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid
    private fun col() = db.collection("users").document(uid ?: error("Пользователь не авторизован")).collection("tasks")

    fun getTodayTasks(): Flow<List<Task>> = callbackFlow {
        val currentUid = auth.currentUser?.uid
        if (currentUid == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0);      cal.set(Calendar.MILLISECOND, 0)
        val start = Timestamp(cal.time)
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val end = Timestamp(cal.time)

        val sub = db.collection("users").document(currentUid).collection("tasks")
            .whereGreaterThanOrEqualTo("dueDate", start)
            .whereLessThanOrEqualTo("dueDate", end)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                trySend(snap?.toObjects(Task::class.java) ?: emptyList())
            }
        awaitClose { sub.remove() }
    }

    fun getAllTasks(): Flow<List<Task>> = callbackFlow {
        val currentUid = auth.currentUser?.uid
        if (currentUid == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val sub = db.collection("users").document(currentUid).collection("tasks")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) { close(e); return@addSnapshotListener }
                trySend(snap?.toObjects(Task::class.java) ?: emptyList())
            }
        awaitClose { sub.remove() }
    }

    suspend fun addTask(task: Task): Result<String> = runCatching {
        val currentUid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Необходимо войти в аккаунт для создания задачи"))
        val userCol = db.collection("users").document(currentUid).collection("tasks")
        val ref = userCol.document()
        ref.set(task.copy(id = ref.id, ownerId = currentUid)).await()
        ref.id
    }.mapToRussianError()

    suspend fun updateTask(task: Task): Result<Unit> = runCatching {
        col().document(task.id).set(task).await()
    }.mapToRussianError()

    suspend fun deleteTask(id: String): Result<Unit> = runCatching {
        col().document(id).delete().await()
    }.mapToRussianError()

    suspend fun completeTask(id: String, byName: String): Result<Unit> = runCatching {
        col().document(id).update(
            mapOf("isCompleted" to true, "completedBy" to uid, "completedByName" to byName)
        ).await()
    }.mapToRussianError()
}

private fun <T> Result<T>.mapToRussianError(): Result<T> {
    val ex = exceptionOrNull() ?: return this
    val msg = ex.message ?: "Неизвестная ошибка"
    val russianMsg = when {
        "PERMISSION_DENIED" in msg ->
            "Нет прав доступа. Убедитесь, что правила Firestore опубликованы (firebase deploy --only firestore:rules)"
        "NOT_FOUND" in msg -> "Документ не найден"
        "UNAVAILABLE" in msg || "network" in msg.lowercase() -> "Нет подключения к сети"
        "UNAUTHENTICATED" in msg -> "Необходимо войти в аккаунт"
        "RESOURCE_EXHAUSTED" in msg -> "Превышен лимит запросов. Попробуйте позже"
        "DEADLINE_EXCEEDED" in msg -> "Превышено время ожидания. Проверьте интернет"
        else -> msg
    }
    return Result.failure(Exception(russianMsg))
}
