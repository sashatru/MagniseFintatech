package com.magnise.fintatech.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Instrument(
    val id: String,
    val symbol: String,
    val kind: String,
    val description: String,
    val tickSize: Double,
    val currency: String,
    val baseCurrency: String,
    val mappings: Mappings,
    val profile: Profile
)

@Serializable
data class Mappings(
    @SerialName("active-tick") val activeTick: ProviderMapping? = null,
    val simulation: ProviderMapping? = null,
    val oanda: ProviderMapping? = null,
    val dxfeed: ProviderMapping? = null
)

@Serializable
data class ProviderMapping(
    val symbol: String,
    val exchange: String? = null,
    val defaultOrderSize: Int
)

@Serializable
data class Profile(
    val name: String,
    val gics: Gics
)

@Serializable
data class Gics(
    val sector: String = ""
)
