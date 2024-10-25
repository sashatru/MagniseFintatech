package com.magnise.fintatech.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnise.fintatech.domain.usecase.LoginUseCase
import com.magnise.fintatech.utils.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class MarketViewModel(
    private val loginUseCase: LoginUseCase,
    //private val refreshTokenUseCase: RefreshTokenUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    open val authState: StateFlow<AuthState> = _authState

    fun authenticateUser(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginUseCase(username, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value =
                    AuthState.Error(result.exceptionOrNull()?.message ?: "Authentication failed")
            }
        }
    }

    //TODO Refresh Token
    /*    fun refreshToken() {
            viewModelScope.launch {
                val result = refreshTokenUseCase()
                if (result.isSuccess) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }*/
}
