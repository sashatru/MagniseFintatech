package com.magnise.fintatech.domain.usecases

import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.remote.api.ApiManager

class GetInstrumentsUseCase(private val apiManager: ApiManager) {

    suspend fun execute(): Result<List<Instrument>> {
        return apiManager.fetchInstruments()
    }
}
