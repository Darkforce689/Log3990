package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject

const val LobbyPath = "/newGame"

private const val URL = BuildConfig.COMMUNICATION_URL

object LobbySocketHandler {

    private lateinit var socket: Socket

    @Synchronized
    fun setSocket() {
        val opts = IO.Options()
        opts.path = LobbyPath
        val cookie = ScrabbleHttpClient.getAuthCookie()

        val headers: MutableMap<String, List<String>> = mutableMapOf()
        headers["Cookie"] = listOf(cookie.toString())

        opts.extraHeaders = headers
        try {
            opts.transports = arrayOf(WebSocket.NAME)
            opts.auth
            socket = IO.socket(URL, opts)
            socket.on(Socket.EVENT_CONNECT) { println("LobbySocketHandler Connected") }
            socket.on(Socket.EVENT_DISCONNECT) { println("LobbySocketHandler Disconnected") }
        } catch (e: Throwable) {
            println("LobbySocketHandler Error : $e")
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
    fun <T> on(event: OnLobbyEvent, callback: (formattedContent: T?) -> Unit) {
        socket.on(event.eventName) { args ->
            val contentType = LobbyEventTypes[event] as Class<T>
            val formattedContent = formatResponse(args, contentType)
            callback(formattedContent)
        }
    }

    @Synchronized
    fun <T> emit(event: EmitLobbyEvent, content: T) {
        val contentType = LobbyEventTypes[event] as Class<T>
        val formattedContent = formatRequest(content, contentType)
        socket.emit(event.eventName, formattedContent)
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
