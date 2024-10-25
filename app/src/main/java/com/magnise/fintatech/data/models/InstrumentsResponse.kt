package com.magnise.fintatech.data.models

import kotlinx.serialization.Serializable

@Serializable
data class InstrumentsResponse(
    val paging: Paging,
    val data: List<Instrument>
)

@Serializable
data class Paging(
    val page: Int,
    val pages: Int,
    val items: Int
)
