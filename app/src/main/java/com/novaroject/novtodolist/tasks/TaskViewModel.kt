package com.novaroject.novtodolist.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.novaroject.novtodolist.data.model.Task
import com.novaroject.novtodolist.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _today = MutableStateFlow(TasksUiState())
    val todayState: StateFlow<TasksUiState> = _today

    private val _all = MutableStateFlow(TasksUiState())
    val allState: StateFlow<TasksUiState> = _all

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
            repo.addTask(Task(title = title, description = description, dueDate = dueDate, priority = priority))
        }

    fun completeTask(id: String) = viewModelScope.launch {
        val name = auth.currentUser?.displayName ?: "Пользователь"
        repo.completeTask(id, name)
    }

    fun deleteTask(id: String) = viewModelScope.launch { repo.deleteTask(id) }

    fun updateTask(task: Task) = viewModelScope.launch { repo.updateTask(task) }
}
