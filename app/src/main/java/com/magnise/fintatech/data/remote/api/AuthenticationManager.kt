package com.magnise.fintatech.data.remote.api

import com.magnise.fintatech.data.models.HistoricalDataResponse
import com.magnise.fintatech.data.models.HistoricalPriceData
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.InstrumentsResponse
import com.magnise.fintatech.data.models.TokenResponse
import com.magnise.fintatech.data.repository.TokenRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import timber.log.Timber

class AuthenticationManager(
    private val client: HttpClient,
    private val tokenRepository: TokenRepository
) {

    companion object {
        const val TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val BASE_URL = "https://platform.fintacharts.com"
    }

    // Function to get the access token using username and password
    suspend fun getToken(username: String, password: String): Boolean {
        try {
            val response: HttpResponse = client.submitForm(
                url = "$BASE_URL/identity/realms/fintatech/protocol/openid-connect/token",
                formParameters = Parameters.build {
                    append("grant_type", "password")
                    append("client_id", "app-cli")
                    append("username", username)
                    append("password", password)
                }
            ) {
                // Set the Content-Type to application/x-www-form-urlencoded
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val tokenResponse: TokenResponse = response.receive()
                tokenRepository.saveTokens(tokenResponse.access_token, tokenResponse.refresh_token)
                return true
            }
        } catch (e: Exception) {
            Timber.tag("Authentication").e("Failed to authenticate: %s", e.localizedMessage)
        }
        return false
    }

    suspend fun fetchInstruments(): Result<List<Instrument>> {
        return try {
            val accessToken = tokenRepository.getAccessToken()
                ?: throw IllegalStateException("Access token not found. Please authenticate first.")

            val instrumentsResponse: InstrumentsResponse = client.get("$BASE_URL/api/instruments/v1/instruments") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                parameter("provider", "oanda")
                parameter("kind", "forex")
            }

            Result.success(instrumentsResponse.data)
        } catch (e: Exception) {
            Timber.tag("Authentication").e("Failed to fetchInstruments: %s", e.localizedMessage)
            Result.failure(e)
        }
    }

    suspend fun getHistoricalData(instrumentId: String, interval: String = "1", periodicity: String = "minute", barsCount: Int = 10): Result<List<HistoricalPriceData>> {
        return try {
            val accessToken = tokenRepository.getAccessToken()
                ?: throw IllegalStateException("Access token not found. Please authenticate first.")

            val response: HistoricalDataResponse = client.get("$BASE_URL/api/bars/v1/bars/count-back") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                parameter("instrumentId", instrumentId)
                parameter("provider", "oanda")
                parameter("interval", interval)
                parameter("periodicity", periodicity)
                parameter("barsCount", barsCount)
            }

            val historicalData = response.data.map {
                HistoricalPriceData(
                    timestamp = it.t,
                    closePrice = it.c
                )
            }

            Result.success(historicalData)
        } catch (e: Exception) {
            Timber.tag("Authentication").e("Failed to fetch historical data: %s", e.localizedMessage)
            Result.failure(e)
        }
    }
}

