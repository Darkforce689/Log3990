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

    private lateinit var socket: Socket

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
            socket = IO.socket(URL, opts)
            socket.on(Socket.EVENT_CONNECT) { println("GameSocketHandler Connected") }
            socket.on(Socket.EVENT_DISCONNECT) { println("GameSocketHandler Disconnected") }
        } catch (e: Throwable) {
            println("GameSocketHandler Error : $e")
        }
    }

    @Synchronized
    fun ensureConnection() {
        if (socket.connected()) {
            return
        }
        socket.connect()
    }

    @Synchronized
    fun <T> on(event: OnGameEvent, contentType: Class<T>, callback: (formattedContent: T?) -> Unit) {
        socket.on(event.event) { args ->
            val formattedContent = formatResponse(args, contentType)
            callback(formattedContent)
        }
    }

    @Synchronized
    fun <T> emit(event: EmitGameEvent, contentType: Class<T>, content: T) {
        val formattedContent = formatRequest(content, contentType)
        socket.emit(event.event, formattedContent)
    }

    @Synchronized
    fun closeConnection() {
        socket.disconnect()
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

    private fun <T> formatRequest(content: T, type: Class<T>): JSONObject? {
        return try {
            JSONObject(Gson().toJson(content))
        } catch (e: Exception) {
            println("Error -> formatRequest -> $type -> content:$content")
            println(e)
            null
        }
    }
}
