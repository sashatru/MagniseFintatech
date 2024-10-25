package com.magnise.fintatech.data.repository

import androidx.security.crypto.EncryptedSharedPreferences
import timber.log.Timber

class TokenRepository(private val sharedPreferences: EncryptedSharedPreferences) {

    companion object {
        const val TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    // Function to save access and refresh tokens securely
    fun saveTokens(accessToken: String, refreshToken: String) {
        Timber.tag("Authentication").d("saveTokens: accessToken: %s, refreshToken: %s", accessToken, refreshToken)
        with(sharedPreferences.edit()) {
            putString(TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    fun clearTokens() {
        with(sharedPreferences.edit()) {
            remove(TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            apply()
        }
    }
}
