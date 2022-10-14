package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.message.model.User
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONArray
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

    @JvmName("onObject")
    @Synchronized
    fun <T> on(event: OnLobbyEvent, callback: (formattedContent: T?) -> Unit) {
        socket.on(event.eventName) { args ->
            val contentType = LobbyEventTypes[event]!!
            val formattedContent = formatObjectResponse(args, contentType as Class<T>)
            callback(formattedContent)
        }
    }

    @JvmName("onArray")
    @Synchronized
    fun <T> on(event: OnLobbyEvent, callback: (formattedContent: List<T>?) -> Unit) {
        socket.on(event.eventName) { args ->
            val contentType = LobbyEventTypes[event]!!
            val formattedContent = formatArrayResponse(args, contentType as Class<Array<T>>)
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

    private fun <T> formatArrayResponse(args: Array<Any>, type: Class<Array<T>>): List<T>? {
        return try {
            Gson().fromJson(args[0].toString(), type).toList()
        } catch (e: Exception) {
            println("Error -> formatArrayResponse -> $type -> args:$args")
            println(e)
            null
        }
    }

    private fun <T> formatObjectResponse(args: Array<Any>, type: Class<T>): T? {
        return try {
            Gson().fromJson(args[0].toString(), type) as T
        } catch (e: Exception) {
            println("Error -> formatObjectResponse -> $type -> args:$args")
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
