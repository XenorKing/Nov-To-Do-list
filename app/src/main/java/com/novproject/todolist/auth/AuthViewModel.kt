package com.novproject.todolist.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novproject.todolist.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            authRepo.login(email, password)
                .onSuccess { _state.value = AuthState(success = true) }
                .onFailure { _state.value = AuthState(error = it.localizedMessage ?: "Ошибка входа") }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            authRepo.register(name, email, password)
                .onSuccess { _state.value = AuthState(success = true) }
                .onFailure { _state.value = AuthState(error = it.localizedMessage ?: "Ошибка регистрации") }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _state.value = AuthState(loading = true)
            authRepo.resetPassword(email)
                .onSuccess { _state.value = AuthState(success = true) }
                .onFailure { _state.value = AuthState(error = it.localizedMessage ?: "Ошибка") }
        }
    }
}
