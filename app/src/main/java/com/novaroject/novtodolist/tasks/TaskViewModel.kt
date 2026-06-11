package com.novaroject.novtodolist.tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.novaroject.novtodolist.data.model.Task
import com.novaroject.novtodolist.data.repository.TaskRepository
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

    fun addTask(title: String, description: String, dueDate: Timestamp?, priority: Int) =
        viewModelScope.launch {
            _addResult.value = null
            repo.addTask(Task(title = title, description = description, dueDate = dueDate, priority = priority))
                .onSuccess { taskId ->
                    _addResult.value = "OK"
                    if (dueDate != null) {
                        TaskReminderWorker.schedule(
                            context       = context,
                            taskId        = taskId,
                            title         = title,
                            dueDateMillis = dueDate.toDate().time
                        )
                    }
                }
                .onFailure { _addResult.value = it.message ?: "Ошибка сохранения задачи" }
        }

    fun clearAddResult() { _addResult.value = null }

    fun completeTask(id: String) = viewModelScope.launch {
        val name = auth.currentUser?.displayName ?: "Пользователь"
        repo.completeTask(id, name)
        TaskReminderWorker.cancel(context, id)
    }

    fun deleteTask(id: String) = viewModelScope.launch {
        repo.deleteTask(id)
        TaskReminderWorker.cancel(context, id)
    }

    fun updateTask(task: Task) = viewModelScope.launch { repo.updateTask(task) }
}
