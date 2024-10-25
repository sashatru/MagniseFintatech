package com.magnise.fintatech.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.domain.usecase.GetInstrumentsUseCase
import com.magnise.fintatech.domain.usecase.LoginUseCase
import com.magnise.fintatech.utils.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

open class MarketViewModel(
    private val loginUseCase: LoginUseCase,
    private val getInstrumentsUseCase: GetInstrumentsUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    open val authState: StateFlow<AuthState> = _authState

    private val _instruments = MutableStateFlow<List<Instrument>>(emptyList())
    val instruments: StateFlow<List<Instrument>> = _instruments

    fun authenticateUser(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginUseCase(username, password)
            if (result.isSuccess) {
                fetchInstruments()
            } else {
                _authState.value =
                    AuthState.Error(result.exceptionOrNull()?.message ?: "Authentication failed")
            }
        }
    }

    private suspend fun fetchInstruments() {
        val instrumentsResult = getInstrumentsUseCase.execute()
        Timber.tag("Authentication").i("MVM instrumentsResult.isSuccess: %s", instrumentsResult.isSuccess)
        if (instrumentsResult.isSuccess) {
            _instruments.value = instrumentsResult.getOrThrow()
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Error("Failed to fetch instruments")
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
