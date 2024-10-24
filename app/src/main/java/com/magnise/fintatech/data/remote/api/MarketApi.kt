package com.magnise.fintatech.data.remote.api

import com.magnise.fintatech.data.models.HistoricalPrice
import com.magnise.fintatech.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MarketApi(private val client: HttpClient) {
    suspend fun getHistoricalData(
        assetId: String,
        provider: String = "oanda",
        interval: String = "1",
        periodicity: String = "minute",
        barsCount: String = "10"
    ): List<HistoricalPrice> {
        return client.get<List<HistoricalPrice>>("${Constants.BASE_URL}/api/bars/v1/bars/count-back") {
            parameter("instrumentId", assetId)
            parameter("provider", provider)
            parameter("interval", interval)
            parameter("periodicity", periodicity)
            parameter("barsCount", barsCount)
        }
    }
}
