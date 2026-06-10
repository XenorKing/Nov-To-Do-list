package com.novproject.todolist.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.novproject.todolist.data.model.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: ""

    private fun tasksCollection() = firestore.collection("users").document(uid).collection("tasks")

    fun getTodayTasks(): Flow<List<Task>> = callbackFlow {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = Timestamp(cal.time)

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfDay = Timestamp(cal.time)

        val listener = tasksCollection()
            .whereGreaterThanOrEqualTo("dueDate", startOfDay)
            .whereLessThanOrEqualTo("dueDate", endOfDay)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tasks = snapshot?.toObjects(Task::class.java) ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    fun getAllTasks(): Flow<List<Task>> = callbackFlow {
        val listener = tasksCollection()
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tasks = snapshot?.toObjects(Task::class.java) ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addTask(task: Task): Result<String> = runCatching {
        val ref = tasksCollection().document()
        ref.set(task.copy(id = ref.id, ownerId = uid)).await()
        ref.id
    }

    suspend fun updateTask(task: Task): Result<Unit> = runCatching {
        tasksCollection().document(task.id).set(task).await()
    }

    suspend fun deleteTask(taskId: String): Result<Unit> = runCatching {
        tasksCollection().document(taskId).delete().await()
    }

    suspend fun completeTask(taskId: String, completedByName: String): Result<Unit> = runCatching {
        tasksCollection().document(taskId).update(
            mapOf(
                "isCompleted" to true,
                "completedBy" to uid,
                "completedByName" to completedByName
            )
        ).await()
    }
}
