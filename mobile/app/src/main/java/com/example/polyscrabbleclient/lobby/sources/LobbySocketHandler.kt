package com.example.polyscrabbleclient.lobby.sources

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.SocketHandler
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket


const val LobbyPath = "/newGame"

private const val URL = BuildConfig.COMMUNICATION_URL

object LobbySocketHandler: SocketHandler(LobbyEventTypes) {

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
    fun closeConnection() {
        socket.disconnect()
    }
}
