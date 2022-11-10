package com.example.polyscrabbleclient.message

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.message.model.BaseMessage
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject
import java.net.URISyntaxException


enum class EventType(val event: String) {
    USER_NAME("userName"),
    JOIN_ROOM("joinRoom"),
    NEW_MESSAGE("newMessage"),
    ROOM_MESSAGES("roomMessages"),
    LEAVE_ROOM("leaveRoom"),
}

private const val URL = BuildConfig.COMMUNICATION_URL

object ChatSocketHandler {

    private lateinit var webSocket: Socket
    private val joinedRooms: MutableSet<String> = HashSet()

    @Synchronized
    fun setSocket() {
        val opts = IO.Options()
        opts.path = "/messages"
        val cookie = ScrabbleHttpClient.getAuthCookie()

        val headers: MutableMap<String, List<String>> = mutableMapOf()
        headers["Cookie"] = listOf(cookie.toString())

        opts.extraHeaders = headers
        try {
            opts.transports = arrayOf(WebSocket.NAME)
            webSocket = IO.socket(URL, opts)
            webSocket.on(Socket.EVENT_CONNECT) { println("Connected") }
            webSocket.on(Socket.EVENT_DISCONNECT) { println("Disconnected") }
        } catch (e: URISyntaxException) {
            println("Error$e")
        }
    }

    @Synchronized
    fun socketConnection() {
        if (webSocket.connected()) {
            return
        }
        webSocket.connect()
    }

    fun setJoinedRooms(roomToJoin: List<String>) {
        val roomIds = HashSet(roomToJoin)
        val roomToLeave = joinedRooms.filter { roomId -> roomIds.contains(roomId) }
        leaveRooms(roomToLeave)
        joinRooms(roomToJoin)
    }

    @Synchronized
    fun leaveRooms(roomIds: List<String>) {
        roomIds.forEach {
            leaveRoom(it)
        }
    }

    @Synchronized
    fun joinRooms(roomIds: List<String>) {
        roomIds.forEach {
            joinRoom(it)
        }
    }

    @Synchronized
    fun joinRoom(roomId: String) {
        if (joinedRooms.contains(roomId)) {
            return
        }
        webSocket.emit(EventType.JOIN_ROOM.event, roomId)
        joinedRooms.add(roomId)
    }

    @Synchronized
    fun leaveRoom(roomId: String) {
        if (!joinedRooms.contains(roomId)) {
            return
        }
        webSocket.emit(EventType.LEAVE_ROOM.event, roomId)
        joinedRooms.remove(roomId)
    }

    fun on(event: EventType, callback: Emitter.Listener) {
        try {
            webSocket.on(event.event, callback)
        } catch (e: Exception) {
        }
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
