package com.magnise.fintatech.data.models

data class HistoricalPrice(
    val t: String,  // Timestamp
    val o: Double,  // Open
    val h: Double,  // High
    val l: Double,  // Low
    val c: Double,  // Close
    val v: Int      // Volume
)