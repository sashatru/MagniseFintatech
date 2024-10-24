package com.magnise.fintatech.data.remote

import androidx.security.crypto.EncryptedSharedPreferences
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import timber.log.Timber

class AuthenticationManager(
    private val client: HttpClient,
    private val sharedPreferences: EncryptedSharedPreferences
) {

    companion object {
        const val TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val BASE_URL = "https://platform.fintacharts.com"
    }

    // Function to get the access token using username and password
    suspend fun getToken(username: String, password: String): Boolean {
        try {
            val response: HttpResponse =
                client.post("$BASE_URL/identity/realms/fintatech/protocol/openid-connect/token") {
                    parameter("grant_type", "password")
                    parameter("client_id", "app-cli")
                    parameter("username", username)
                    parameter("password", password)
                }

            if (response.status == HttpStatusCode.OK) {
                val tokenResponse: TokenResponse = response.receive()
                saveTokens(tokenResponse.access_token, tokenResponse.refresh_token)
                return true
            }
        } catch (e: Exception) {
            Timber.tag("AuthError").e("Failed to authenticate: %s", e.localizedMessage)
        }
        return false
    }

    // Function to save access and refresh tokens securely
    private fun saveTokens(accessToken: String, refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString(TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }

    // Function to retrieve the stored access token
    fun getAccessToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    // Function to retrieve the stored refresh token
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    // Function to refresh the access token using the refresh token
    suspend fun refreshToken(): Boolean {
        val refreshToken = getRefreshToken() ?: return false
        try {
            val response: HttpResponse =
                client.post("$BASE_URL/identity/realms/fintatech/protocol/openid-connect/token") {
                    parameter("grant_type", "refresh_token")
                    parameter("client_id", "app-cli")
                    parameter("refresh_token", refreshToken)
                }

            if (response.status == HttpStatusCode.OK) {
                val tokenResponse: TokenResponse = response.receive()
                saveTokens(tokenResponse.access_token, tokenResponse.refresh_token)
                return true
            }
        } catch (e: Exception) {
            Timber.tag("AuthError").e("Failed to refresh token: %s", e.localizedMessage)
        }
        return false
    }
}

// Data class to parse the token response
data class TokenResponse(
    val access_token: String,
    val refresh_token: String
)
