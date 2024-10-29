package com.magnise.fintatech.data.models

import kotlinx.serialization.Serializable

@Serializable
data class HistoricalDataResponse(
    val data: List<HistoricalPriceEntry>
)

@Serializable
data class HistoricalPriceEntry(
    val t: String, // Timestamp
    val c: Double // Close price
)
