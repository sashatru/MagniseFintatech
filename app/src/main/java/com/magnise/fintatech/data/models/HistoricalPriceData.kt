package com.magnise.fintatech.data.models

import kotlinx.serialization.Serializable

data class HistoricalPriceData(
    val timestamp: String, // "t" field from the response
    val closePrice: Double // "c" field from the response
)