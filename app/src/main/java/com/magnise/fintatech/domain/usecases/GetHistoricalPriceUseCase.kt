package com.magnise.fintatech.domain.usecases

import com.magnise.fintatech.data.models.HistoricalPriceData
import com.magnise.fintatech.data.remote.api.AuthenticationManager

class GetHistoricalPriceUseCase(
    private val authenticationManager: AuthenticationManager
) {
    suspend operator fun invoke(instrumentId: String, count: Int): Result<List<HistoricalPriceData>> {
        return try {
            authenticationManager.getHistoricalData(instrumentId, barsCount = count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
