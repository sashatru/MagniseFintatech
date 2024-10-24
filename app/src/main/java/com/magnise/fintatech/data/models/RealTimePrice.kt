package com.magnise.fintatech.data.models

data class RealTimePrice(
    val ask: PriceData?,
    val bid: PriceData?,
    val last: PriceData?,
    val change: Double?,
    val changePct: Double?
)

data class PriceData(
    val timestamp: String,
    val price: Double,
    val volume: Int
)
