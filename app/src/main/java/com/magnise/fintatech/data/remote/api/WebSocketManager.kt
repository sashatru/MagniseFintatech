package com.magnise.fintatech.data.remote.api

import com.magnise.fintatech.data.models.PriceData
import com.magnise.fintatech.data.models.WebSocketMessage
import com.magnise.fintatech.data.repository.TokenRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber

class WebSocketManager(
    private val tokenRepository: TokenRepository
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var reconnectJob: Job? = null

    private val _realTimePrice = MutableStateFlow<PriceData?>(null)
    val realTimePrice = _realTimePrice.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun connect() {
        reconnectJob?.cancel()
        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val token = tokenRepository.getAccessToken()
                    val request = Request.Builder()
                        .url("wss://platform.fintacharts.com/api/streaming/ws/v1/realtime?token=$token")
                        .build()

                    webSocket = client.newWebSocket(request, FintaWebSocketListener())
                    break // Exit loop on successful connection
                } catch (e: Exception) {
                    Timber.tag("WebSocket").e("Connection failed: ${e.localizedMessage}")
                    delay(5000) // Retry delay
                }
            }
        }
    }

    private inner class FintaWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            Timber.tag("WebSocket").i("Connection opened")
            sendSubscriptionMessage()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Timber.tag("WebSocket").i("Message received: $text")
            handleIncomingMessage(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            handleIncomingMessage(bytes.utf8())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Timber.tag("WebSocket").i("Closing: $reason")
            webSocket.close(1000, null)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            Timber.tag("WebSocket").e("WebSocket error: ${t.localizedMessage}")
            CoroutineScope(Dispatchers.IO).launch { reconnect() }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Timber.tag("WebSocket").i("WebSocket closed: $reason")
            CoroutineScope(Dispatchers.IO).launch { reconnect() }
        }
    }

    private suspend fun reconnect() {
        delay(5000) // Reconnection delay
        connect()
    }

    private fun sendSubscriptionMessage() {
        val subscriptionMessage = """
            {
                "type": "l1-subscription",
                "id": "1",
                "instrumentId": "ad9e5345-4c3b-41fc-9437-1d253f62db52",
                "provider": "simulation",
                "subscribe": true,
                "kinds": ["ask", "bid", "last"]
            }
        """.trimIndent()
        webSocket?.send(subscriptionMessage)
    }

    private fun handleIncomingMessage(responseText: String) {
        Timber.tag("WebSocket").i("WSM handleIncomingMessage responseText: %s", responseText)
        try {
            val message = json.decodeFromString<WebSocketMessage>(responseText)

            // Process only messages with a `last` field
            if (message.type == "l1-update" || message.type == "l1-snapshot") {
                message.last?.let { lastPriceData ->
                    Timber.tag("WebSocket").d("WSM handleIncomingMessage update last data: %s", lastPriceData)
                    _realTimePrice.value = lastPriceData
                }
            } else {
                Timber.tag("WebSocket").i("Ignored message type: ${message.type}")
            }
        } catch (e: Exception) {
            Timber.tag("WebSocket").e("WSM handleIncomingMessage error: %s", e.localizedMessage)
        }
    }


    suspend fun disconnect() {
        reconnectJob?.cancel()
        webSocket?.close(1000, "Disconnecting")
    }
}
