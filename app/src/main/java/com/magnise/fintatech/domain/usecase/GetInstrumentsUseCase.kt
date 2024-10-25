package com.magnise.fintatech.domain.usecase

import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.remote.api.AuthenticationManager

class GetInstrumentsUseCase(private val authenticationManager: AuthenticationManager) {

    suspend fun execute(): Result<List<Instrument>> {
        return authenticationManager.fetchInstruments()
    }
}
