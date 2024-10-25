package com.magnise.fintatech.data.models

import kotlinx.serialization.Serializable

// Data class to parse the token response
@Suppress("PropertyName")
@Serializable
data class TokenResponse(
    val access_token: String,
    val refresh_token: String
)