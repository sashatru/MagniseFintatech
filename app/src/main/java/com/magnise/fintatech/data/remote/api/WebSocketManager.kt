package com.magnise.fintatech.data.remote.api

import com.magnise.fintatech.data.models.PriceData
import com.magnise.fintatech.data.models.WebSocketMessage
import com.magnise.fintatech.data.repository.TokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

    private lateinit var selectedInstrumentId: String

    private val json = Json { ignoreUnknownKeys = true }

    private var shouldReconnect = true
    private var isReconnecting = false

    suspend fun connect(selectedInstrumentId: String) {
        this.selectedInstrumentId = selectedInstrumentId
        // Disconnect any existing session
        disconnect()
        shouldReconnect = true // Enable reconnection for unexpected disconnects

        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive && shouldReconnect && !isReconnecting) {
                isReconnecting = true
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
            //Timber.tag("WebSocket").i("Message received: $text")
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
            Timber.tag("WebSocket").e("WebSocket error: ${t}")
            if (shouldReconnect && !isReconnecting) reconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Timber.tag("WebSocket").i("WebSocket closed: $reason")
            if (shouldReconnect) reconnect()
        }
    }

    private fun reconnect() {
        if (!isReconnecting) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(5000) // Reconnection delay
                connect(selectedInstrumentId)
            }
        }
    }

    private fun sendSubscriptionMessage() {
        val subscriptionMessage = """
            {
                "type": "l1-subscription",
                "id": "1",
                "instrumentId": "$selectedInstrumentId",
                "provider": "simulation",
                "subscribe": true,
                "kinds": ["ask", "bid", "last"]
            }
        """.trimIndent()

        Timber.tag("WebSocket").w("WebSocket subscriptionMessage: $subscriptionMessage")

        webSocket?.send(subscriptionMessage)
        isReconnecting = false
    }

    private fun handleIncomingMessage(responseText: String) {
        //Timber.tag("WebSocket").i("WSM handleIncomingMessage responseText: %s", responseText)
        try {
            val message = json.decodeFromString<WebSocketMessage>(responseText)

            // Process only messages with a `last` field
            if (message.type == "l1-update" || message.type == "l1-snapshot") {
                message.last?.let { lastPriceData ->
                    Timber.tag("WebSocket")
                        .d("WSM handleIncomingMessage update last data: %s", lastPriceData)
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
        Timber.tag("WebSocket").i("Disconnect WebSocket")
        shouldReconnect = false // Disable reconnection for manual disconnects
        reconnectJob?.cancelAndJoin()
        webSocket?.close(1000, "Disconnecting")
        webSocket = null
    }
}
