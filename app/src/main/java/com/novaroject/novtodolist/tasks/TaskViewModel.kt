package com.novaroject.novtodolist.tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.novaroject.novtodolist.data.model.Task
import com.novaroject.novtodolist.data.repository.TaskRepository
import com.novaroject.novtodolist.notifications.ExactAlarmScheduler
import com.novaroject.novtodolist.notifications.TaskReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repo: TaskRepository,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _today = MutableStateFlow(TasksUiState())
    val todayState: StateFlow<TasksUiState> = _today

    private val _all = MutableStateFlow(TasksUiState())
    val allState: StateFlow<TasksUiState> = _all

    private val _addResult = MutableStateFlow<String?>(null)
    val addResult: StateFlow<String?> = _addResult.asStateFlow()

    private val _completeError = MutableStateFlow<String?>(null)
    val completeError: StateFlow<String?> = _completeError.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getTodayTasks()
                .catch { _today.value = TasksUiState(error = it.message, loading = false) }
                .collect { _today.value = TasksUiState(tasks = it, loading = false) }
        }
        viewModelScope.launch {
            repo.getAllTasks()
                .catch { _all.value = TasksUiState(error = it.message, loading = false) }
                .collect { _all.value = TasksUiState(tasks = it, loading = false) }
        }
    }

    fun filteredToday(q: String) = _today.value.tasks.filter {
        q.isBlank() || it.title.contains(q, true) || it.description.contains(q, true)
    }

    fun filteredAll(q: String) = _all.value.tasks.filter {
        q.isBlank() || it.title.contains(q, true) || it.description.contains(q, true)
    }

    fun addTask(
        title: String,
        description: String,
        dueDate: Timestamp?,
        priority: Int,
        category: String = "",
        reminderOffset: Int = 30,
        repeatType: String = "none"
    ) = viewModelScope.launch {
        _addResult.value = null
        val task = Task(
            title = title,
            description = description,
            dueDate = dueDate,
            priority = priority,
            category = category,
            reminderOffset = reminderOffset,
            repeatType = repeatType
        )
        repo.addTask(task)
            .onSuccess { taskId ->
                _addResult.value = "OK"
                if (dueDate != null) {
                    val ms = dueDate.toDate().time
                    // Точный AlarmManager (основной метод)
                    ExactAlarmScheduler.schedule(
                        context = context,
                        taskId = taskId,
                        title = title,
                        dueDateMillis = ms,
                        reminderOffsetMinutes = reminderOffset
                    )
                    // WorkManager как резервный вариант
                    TaskReminderWorker.schedule(
                        context = context,
                        taskId = taskId,
                        title = title,
                        dueDateMillis = ms,
                        reminderOffsetMinutes = reminderOffset
                    )
                }
            }
            .onFailure { _addResult.value = it.message ?: "Ошибка сохранения задачи" }
    }

    fun clearAddResult() { _addResult.value = null }

    fun completeTask(id: String) = viewModelScope.launch {
        _completeError.value = null
        val name = auth.currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Пользователь"
        repo.completeTask(id, name)
            .onSuccess {
                ExactAlarmScheduler.cancel(context, id)
                TaskReminderWorker.cancel(context, id)
            }
            .onFailure { _completeError.value = it.message ?: "Не удалось отметить задачу выполненной" }
    }

    fun clearCompleteError() { _completeError.value = null }

    fun deleteTask(id: String) = viewModelScope.launch {
        repo.deleteTask(id)
        ExactAlarmScheduler.cancel(context, id)
        TaskReminderWorker.cancel(context, id)
    }

    fun updateTask(task: Task) = viewModelScope.launch { repo.updateTask(task) }
}
