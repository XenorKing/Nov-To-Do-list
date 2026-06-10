package com.novproject.todolist.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.novproject.todolist.data.model.Task
import com.novproject.todolist.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val taskRepo: TaskRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _todayState = MutableStateFlow(TasksUiState())
    val todayState: StateFlow<TasksUiState> = _todayState

    private val _allState = MutableStateFlow(TasksUiState())
    val allState: StateFlow<TasksUiState> = _allState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadTodayTasks()
        loadAllTasks()
    }

    private fun loadTodayTasks() {
        viewModelScope.launch {
            taskRepo.getTodayTasks()
                .catch { _todayState.value = TasksUiState(error = it.message) }
                .collect { tasks ->
                    _todayState.value = TasksUiState(tasks = tasks, loading = false)
                }
        }
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            taskRepo.getAllTasks()
                .catch { _allState.value = TasksUiState(error = it.message) }
                .collect { tasks ->
                    _allState.value = TasksUiState(tasks = tasks, loading = false)
                }
        }
    }

    fun setSearch(query: String) {
        _searchQuery.value = query
    }

    fun filteredAll(query: String): List<Task> =
        _allState.value.tasks.filter {
            query.isBlank() || it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }

    fun filteredToday(query: String): List<Task> =
        _todayState.value.tasks.filter {
            query.isBlank() || it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }

    fun addTask(title: String, description: String, dueDate: Timestamp?, priority: Int) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority
            )
            taskRepo.addTask(task)
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val name = auth.currentUser?.displayName ?: "Пользователь"
            taskRepo.completeTask(taskId, name)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepo.deleteTask(taskId)
        }
    }
}
