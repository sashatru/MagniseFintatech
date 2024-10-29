package com.magnise.fintatech.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.PriceData
import com.magnise.fintatech.domain.usecase.GetInstrumentsUseCase
import com.magnise.fintatech.domain.usecase.GetRealDataUseCase
import com.magnise.fintatech.domain.usecase.LoginUseCase
import com.magnise.fintatech.utils.AuthState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

const val USER_NAME: String = "r_test@fintatech.com"
const val PASSWORD: String = "kisfiz-vUnvy9-sopnyv"

open class MarketViewModel(
    private val loginUseCase: LoginUseCase,
    private val getInstrumentsUseCase: GetInstrumentsUseCase,
    private val getRealDataUseCase: GetRealDataUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    open val authState: StateFlow<AuthState> = _authState

    private val _instruments = MutableStateFlow<List<Instrument>>(emptyList())
    val instruments: StateFlow<List<Instrument>> = _instruments


    var selectedInstrumentId by mutableStateOf<String?>(null)
    var selectedInstrumentSymbol by mutableStateOf<String?>(null)

    val realTimePrice: StateFlow<PriceData?> = getRealDataUseCase.realTimePrice

    init {
        authenticateUser(USER_NAME, PASSWORD)
    }

    fun authenticateUser(username: String, password: String) {
        Timber.tag("Authentication")
            .i("MVM authenticateUser")

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
        Timber.tag("Authentication")
            .i("MVM instrumentsResult.isSuccess: %s", instrumentsResult.isSuccess)
        if (instrumentsResult.isSuccess) {
            _instruments.value = instrumentsResult.getOrThrow()
            if (selectedInstrumentId == null && selectedInstrumentSymbol == null) {
                _instruments.value.firstOrNull()?.let { instrument ->
                    selectedInstrumentId = instrument.id
                    selectedInstrumentSymbol = instrument.symbol
                }
            }
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Error("Failed to fetch instruments")
        }
    }

    fun selectInstrument(instrument: Instrument) {
        selectedInstrumentId = instrument.id
        selectedInstrumentSymbol = instrument.symbol
    }

    suspend fun startFetchData(selectedInstrumentId: String) {
        viewModelScope.launch {
            getRealDataUseCase.connect(selectedInstrumentId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopFetchData()
    }

    private fun stopFetchData() {
        viewModelScope.launch {
            getRealDataUseCase.disconnect()
        }
    }

}
