package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject

const val GamePath = "/gameTest"

abstract class OnEventContent
abstract class EmitEventContent

enum class OnGameEvent (val event: String) {
    StartTime("timerStartingTime"),
    RemainingTime("timeUpdate"),
    GameState("gameState"),
}

enum class EmitGameEvent (val event: String) {
    JoinGame("joinGame"),
    NextAction("nextAction"),
    Disconnect("disconnect"),
}

private const val URL = BuildConfig.COMMUNICATION_URL

object GameSocketHandler {

    private lateinit var _socket: Socket

    @Synchronized
    fun setSocket() {
        val opts = IO.Options()
        opts.path = GamePath
        val cookie = ScrabbleHttpClient.getAuthCookie()

        val headers: MutableMap<String, List<String>> = mutableMapOf()
        headers["Cookie"] = listOf(cookie.toString())

        opts.extraHeaders = headers
        try {
            opts.transports = arrayOf(WebSocket.NAME)
            opts.auth
            _socket = IO.socket(URL, opts)
            _socket.on(Socket.EVENT_CONNECT) { println("GameSocketHandler Connected") }
            _socket.on(Socket.EVENT_DISCONNECT) { println("GameSocketHandler Disconnected") }
        } catch (e: Throwable) {
            println("GameSocketHandler Error : $e")
        }
    }

    @Synchronized
    fun ensureConnection() {
        if (_socket.connected()) {
            return
        }
        _socket.connect()
    }

    @Synchronized
    fun <T> on(event: OnGameEvent, contentType: Class<T>, callback: (formattedContent: T?) -> Unit) {
        _socket.on(event.event) { content ->
            val formattedContent = formatResponse(content, contentType)
            callback(formattedContent)
        }
    }

    @Synchronized
    fun <T> emit(event: EmitGameEvent, contentType: Class<T>, content: T) {
        val formattedContent = formatRequest(content, contentType)
        _socket.emit(event.event, formattedContent)
    }

    @Synchronized
    fun closeConnection() {
        _socket.disconnect()
    }

    private fun <T> formatResponse(args: Array<Any>, type: Class<T>): T? {
        return try {
            val data = args[0] as JSONObject
            Gson().fromJson(data.toString(), type) as T
        } catch (e: Exception) {
            println("Error -> formatResponse -> $type -> args:$args")
            println(e)
            null
        }
    }

    private fun <T> formatRequest(content: T, type: Class<T>): String? {
        return try {
            // TODO : FIX. GSON sends """{"a":"1"}"""
            //  instead of """{a:"b"}"""
            Gson().toJsonTree(content, type).toString()
        } catch (e: Exception) {
            println("Error -> formatRequest -> $type -> content:$content")
            println(e)
            null
        }
    }
}
