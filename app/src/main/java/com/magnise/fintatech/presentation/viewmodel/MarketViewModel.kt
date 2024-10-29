package com.magnise.fintatech.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnise.fintatech.data.models.HistoricalPriceData
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.PriceData
import com.magnise.fintatech.domain.usecases.GetHistoricalPriceUseCase
import com.magnise.fintatech.domain.usecases.GetInstrumentsUseCase
import com.magnise.fintatech.domain.usecases.GetRealDataUseCase
import com.magnise.fintatech.domain.usecases.LoginUseCase
import com.magnise.fintatech.utils.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

const val USER_NAME: String = "r_test@fintatech.com"
const val PASSWORD: String = "kisfiz-vUnvy9-sopnyv"

open class MarketViewModel(
    private val loginUseCase: LoginUseCase,
    private val getInstrumentsUseCase: GetInstrumentsUseCase,
    private val getRealDataUseCase: GetRealDataUseCase,
    private val getHistoricalPriceUseCase: GetHistoricalPriceUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    open val authState: StateFlow<AuthState> = _authState

    private val _instruments = MutableStateFlow<List<Instrument>>(emptyList())
    val instruments: StateFlow<List<Instrument>> = _instruments

    private val _historicalPriceData = MutableStateFlow<List<HistoricalPriceData>>(emptyList())
    val historicalPriceData: StateFlow<List<HistoricalPriceData>> = _historicalPriceData



    var selectedInstrumentId by mutableStateOf<String?>(null)
    var selectedInstrumentSymbol by mutableStateOf<String?>(null)

    val realTimePrice: StateFlow<PriceData?> = getRealDataUseCase.realTimePrice

    init {
        authenticateUser(USER_NAME, PASSWORD)
    }

    @Suppress("SameParameterValue")
    private fun authenticateUser(username: String, password: String) {
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
            fetchHistoricalPriceData(selectedInstrumentId)
        }
    }

    private fun fetchHistoricalPriceData(instrumentId: String, count: Int = 10) {
        viewModelScope.launch {
            try {
                val result = getHistoricalPriceUseCase(instrumentId, count)
                // Log success or failure
                result.onSuccess {
                    _historicalPriceData.value = it
                    Timber.tag("GetHistory").d("Successfully fetched historical price data: ${historicalPriceData.value}")
                }.onFailure { exception ->
                    Timber.tag("GetHistory").e("Failed to fetch historical price data: ${exception.localizedMessage}")
                }
            } catch (e: Exception) {
                Timber.tag("GetHistory").e("Unexpected error fetching historical price data: ${e.localizedMessage}")
            }
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
