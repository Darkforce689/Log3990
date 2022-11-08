package com.example.polyscrabbleclient.message

import android.webkit.CookieManager
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.message.model.BaseMessage
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.HashMap


enum class EventType (val event: String){
    USER_NAME("userName"),
    JOIN_ROOM("joinRoom"),
    NEW_MESSAGE("newMessage"),
    ROOM_MESSAGES("roomMessages")
}

private const val URL = BuildConfig.COMMUNICATION_URL

 object ChatSocketHandler {

    private lateinit var webSocket: Socket

    @Synchronized
    fun setSocket() {
        val opts = IO.Options()
        opts.path = "/messages"
        val cookie = ScrabbleHttpClient.getAuthCookie()

        println("cookie 2: " + cookie)

        val headers: MutableMap<String, List<String>> = mutableMapOf()
        headers["Cookie"] = listOf(cookie.toString())

        opts.extraHeaders = headers
        try {
            opts.transports = arrayOf(WebSocket.NAME)
            opts.auth
            webSocket = IO.socket(URL, opts)
            webSocket.on(Socket.EVENT_CONNECT) { println("Connected") }
            webSocket.on(Socket.EVENT_DISCONNECT) { println("Disconnected") }
        } catch (e: URISyntaxException) {
            println("Error$e")
        }
    }

    @Synchronized
    fun socketConnection() {
        if(webSocket.connected()){
            return
        }
        webSocket.connect()
    }

    @Synchronized
    fun joinRoom(roomID: String) {
        webSocket.emit(EventType.JOIN_ROOM.event, roomID)
    }

    fun on(event: EventType, callback: Emitter.Listener) {
        try {
            webSocket.on(event.event, callback)
        } catch (e: Exception){}
    }


    @Synchronized
    fun sendMessage(baseMessage: BaseMessage) {
        val baseMessageJSON = JSONObject(Gson().toJson(baseMessage))
        webSocket.emit(EventType.NEW_MESSAGE.event, baseMessageJSON)
    }

    @Synchronized
    fun closeConnection() {
        webSocket.disconnect()
    }
}
