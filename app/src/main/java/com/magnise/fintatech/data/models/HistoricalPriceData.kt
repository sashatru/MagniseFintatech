package com.magnise.fintatech.data.models

data class HistoricalPriceData(
    val timestamp: String, // "t" field from the response
    val closePrice: Double // "c" field from the response
)