package com.novaroject.novtodolist.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novaroject.novtodolist.data.repository.AuthRepository
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
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    private val _updateNickResult = MutableStateFlow<String?>(null)
    val updateNickResult: StateFlow<String?> = _updateNickResult

    fun login(email: String, password: String) = viewModelScope.launch {
        _state.value = AuthState(loading = true)
        repo.login(email, password)
            .onSuccess { _state.value = AuthState(success = true) }
            .onFailure { _state.value = AuthState(error = friendlyError(it.message)) }
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _state.value = AuthState(loading = true)
        repo.register(name, email, password)
            .onSuccess { _state.value = AuthState(success = true) }
            .onFailure { _state.value = AuthState(error = friendlyError(it.message)) }
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        _state.value = AuthState(loading = true)
        repo.resetPassword(email)
            .onSuccess { _state.value = AuthState(success = true) }
            .onFailure { _state.value = AuthState(error = friendlyError(it.message)) }
    }

    // Fix #6 — обновление ника в профиле
    fun updateNickname(nickname: String) = viewModelScope.launch {
        _updateNickResult.value = "loading"
        repo.updateDisplayName(nickname)
            .onSuccess { _updateNickResult.value = "OK" }
            .onFailure { _updateNickResult.value = it.message ?: "Ошибка обновления" }
    }

    fun clearUpdateNickResult() { _updateNickResult.value = null }

    private fun friendlyError(msg: String?): String = when {
        msg == null -> "Неизвестная ошибка"
        "password" in msg -> "Неверный пароль"
        "email" in msg && "already" in msg -> "Email уже используется"
        "no user" in msg.lowercase() -> "Пользователь не найден"
        "network" in msg.lowercase() -> "Нет подключения к сети"
        else -> msg
    }
}
