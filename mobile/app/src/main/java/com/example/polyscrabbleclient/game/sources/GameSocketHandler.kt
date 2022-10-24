package com.example.polyscrabbleclient.game.sources

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.SocketHandler
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket


const val GamePath = "/game"

private const val URL = BuildConfig.COMMUNICATION_URL

object GameSocketHandler : SocketHandler(GameEventTypes) {

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
    fun closeConnection() {
        socket.disconnect()
    }
}
