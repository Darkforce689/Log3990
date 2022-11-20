package com.example.polyscrabbleclient.auth

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import java.net.URISyntaxException

object AppSocketHandler {
    private lateinit var socket: Socket

    fun setSocket() {
        println("AppSocketHandler Socket set")
        val opts = IO.Options()
        opts.path = "/app"
        val cookie = ScrabbleHttpClient.getAuthCookie()

        val headers: MutableMap<String, List<String>> = mutableMapOf()
        headers["Cookie"] = listOf(cookie.toString())
        opts.extraHeaders = headers

        try {
            opts.transports = arrayOf(WebSocket.NAME)
            socket = IO.socket(BuildConfig.COMMUNICATION_URL, opts)
            socket.on(Socket.EVENT_CONNECT) { println("AppSocketHandler Connected") }
            socket.on(Socket.EVENT_DISCONNECT) { println("AppSocketHandler Disconnected") }
        } catch (e: URISyntaxException) {
            println("Error$e")
        }
    }

    @Synchronized
    fun connect() {
        if (socket.connected()) {
            return
        }
        socket.connect()
    }

    @Synchronized
    fun disconnect() {
        socket.disconnect()
    }
}
