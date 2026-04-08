package com.example.smartclass.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartclass.util.AuthManager
import com.example.smartclass.util.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isSignInMode = MutableStateFlow(true)
    val isSignInMode: StateFlow<Boolean> = _isSignInMode.asStateFlow()

    fun signIn(email: String, password: String) {
        Log.d(TAG, "Попытка входа для: $email")
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            Log.d(TAG, "State: Loading")
            val result = AuthManager.signIn(email, password)
            if (result.isSuccess) {
                Log.d(TAG, "Вход успешен: $email")
                _authState.value = AuthState.Success
            } else {
                val error = result.exceptionOrNull()?.message ?: "Ошибка входа"
                Log.e(TAG, "Ошибка входа: $error")
                _authState.value = AuthState.Error(error)
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        role: UserRole = UserRole.STUDENT,
        grade: Int = 7,
        firstName: String = "",
        lastName: String = ""
    ) {
        Log.d(TAG, "Попытка регистрации для: $email, role: $role, grade: $grade, firstName: $firstName, lastName: $lastName")
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            Log.d(TAG, "State: Loading")
            val result = AuthManager.signUp(email, password, role, grade, firstName, lastName)
            if (result.isSuccess) {
                Log.d(TAG, "Регистрация успешна: $email")
                _authState.value = AuthState.Success
            } else {
                val error = result.exceptionOrNull()?.message ?: "Ошибка регистрации"
                Log.e(TAG, "Ошибка регистрации: $error")
                _authState.value = AuthState.Error(error)
            }
        }
    }

    fun toggleAuthMode() {
        _isSignInMode.value = !_isSignInMode.value
        _authState.value = AuthState.Idle
        Log.d(TAG, "Режим переключён: ${if (_isSignInMode.value) "Вход" else "Регистрация"}")
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        Log.d(TAG, "State сброшен: Idle")
    }

    fun resetPassword(email: String) {
        Log.d(TAG, "Запрос сброса пароля для: $email")
        viewModelScope.launch {
            val result = AuthManager.resetPassword(email)
            if (result.isFailure) {
                Log.e(TAG, "Ошибка сброса пароля: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    class Factory(private val createViewModel: () -> AuthViewModel) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return createViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
