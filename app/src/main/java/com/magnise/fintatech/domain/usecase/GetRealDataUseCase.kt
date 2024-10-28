package com.magnise.fintatech.domain.usecase

import com.magnise.fintatech.data.models.PriceData
import com.magnise.fintatech.data.remote.api.WebSocketManager
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class GetRealDataUseCase(
    private val webSocketManager: WebSocketManager
) {
    val realTimePrice: StateFlow<PriceData?> = webSocketManager.realTimePrice

    suspend fun connect(selectedInstrumentId: String) {
        webSocketManager.connect(selectedInstrumentId)
    }

    suspend fun disconnect() {
        Timber.tag("WebSocket").d("GetRealDataUseCase disconnect WebSocket")
        webSocketManager.disconnect()
    }
}
