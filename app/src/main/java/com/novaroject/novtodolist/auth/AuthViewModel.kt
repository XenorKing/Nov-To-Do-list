package com.novaroject.novtodolist.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.novaroject.novtodolist.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
            .onFailure { _state.value = AuthState(error = translateFirebaseError(it)); scheduleErrorClear() }
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _state.value = AuthState(loading = true)
        repo.register(name, email, password)
            .onSuccess { _state.value = AuthState(success = true) }
            .onFailure { _state.value = AuthState(error = translateFirebaseError(it)); scheduleErrorClear() }
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        if (email.isBlank()) {
            _state.value = AuthState(error = "Введите email")
            scheduleErrorClear()
            return@launch
        }
        _state.value = AuthState(loading = true)
        repo.resetPassword(email)
            .onSuccess { _state.value = AuthState(error = "Письмо отправлено на $email") }
            .onFailure { _state.value = AuthState(error = translateFirebaseError(it)); scheduleErrorClear() }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun scheduleErrorClear() = viewModelScope.launch {
        delay(5000)
        clearError()
    }

    fun updateNickname(nickname: String) = viewModelScope.launch {
        _updateNickResult.value = "loading"
        repo.updateDisplayName(nickname)
            .onSuccess { _updateNickResult.value = "OK" }
            .onFailure { _updateNickResult.value = it.message ?: "Ошибка обновления" }
    }

    fun clearUpdateNickResult() { _updateNickResult.value = null }

    private fun translateFirebaseError(e: Throwable): String {
        if (e is FirebaseAuthException) {
            return when (e.errorCode) {
                "ERROR_INVALID_EMAIL"             -> "Неверный формат email"
                "ERROR_WRONG_PASSWORD"            -> "Неверный пароль"
                "ERROR_USER_NOT_FOUND"            -> "Пользователь с таким email не найден"
                "ERROR_USER_DISABLED"             -> "Аккаунт заблокирован"
                "ERROR_EMAIL_ALREADY_IN_USE"      -> "Email уже используется другим аккаунтом"
                "ERROR_WEAK_PASSWORD"             -> "Пароль слишком простой (минимум 6 символов)"
                "ERROR_NETWORK_REQUEST_FAILED"    -> "Нет подключения к интернету"
                "ERROR_TOO_MANY_REQUESTS"         -> "Слишком много попыток. Попробуйте позже"
                "ERROR_OPERATION_NOT_ALLOWED"     -> "Вход с email/паролем не включён"
                "ERROR_INVALID_CREDENTIAL"        -> "Неверный email или пароль"
                "ERROR_REQUIRES_RECENT_LOGIN"     -> "Требуется повторный вход для этого действия"
                "ERROR_EXPIRED_ACTION_CODE"       -> "Ссылка устарела. Запросите новую"
                "ERROR_INVALID_ACTION_CODE"       -> "Неверная или уже использованная ссылка"
                else -> parseMessageError(e.message)
            }
        }
        return parseMessageError(e.message)
    }

    private fun parseMessageError(message: String?): String {
        if (message == null) return "Неизвестная ошибка"
        return when {
            message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
            message.contains("incorrect, malformed or has expired", ignoreCase = true) ->
                "Неверный email или пароль"
            message.contains("EMAIL_EXISTS", ignoreCase = true) ||
            message.contains("already in use", ignoreCase = true) ->
                "Email уже используется другим аккаунтом"
            message.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "Пароль слишком простой (минимум 6 символов)"
            message.contains("INVALID_EMAIL", ignoreCase = true) ->
                "Неверный формат email"
            message.contains("USER_DISABLED", ignoreCase = true) ->
                "Аккаунт заблокирован"
            message.contains("TOO_MANY_ATTEMPTS", ignoreCase = true) ||
            message.contains("too many requests", ignoreCase = true) ->
                "Слишком много попыток. Попробуйте позже"
            message.contains("NETWORK_ERROR", ignoreCase = true) ||
            message.contains("network", ignoreCase = true) ->
                "Нет подключения к интернету"
            message.contains("USER_NOT_FOUND", ignoreCase = true) ->
                "Пользователь с таким email не найден"
            else -> "Ошибка авторизации. Попробуйте ещё раз"
        }
    }
}
