package com.magnise.fintatech.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PriceData(
    val timestamp: String,
    val price: Double,
    val volume: Int
){
    override fun toString(): String {
        return "PriceData(timestamp='$timestamp', price=${"%.2f".format(price)}, volume=$volume)"
    }
}

@Serializable
data class WebSocketMessage(
    val type: String,
    val last: PriceData? = null
)
