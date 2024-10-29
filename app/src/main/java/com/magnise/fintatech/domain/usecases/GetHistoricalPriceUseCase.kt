package com.magnise.fintatech.domain.usecases

import com.magnise.fintatech.data.models.HistoricalPriceData
import com.magnise.fintatech.data.remote.api.ApiManager

class GetHistoricalPriceUseCase(
    private val apiManager: ApiManager
) {
    suspend operator fun invoke(instrumentId: String, count: Int): Result<List<HistoricalPriceData>> {
        return try {
            apiManager.getHistoricalData(instrumentId, barsCount = count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
