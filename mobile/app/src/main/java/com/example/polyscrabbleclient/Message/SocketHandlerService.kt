package com.example.polyscrabbleclient.Message

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import java.lang.Exception
import java.net.URISyntaxException


enum class EventType (val event: String){
    USER_NAME("userName"),
    JOIN_ROOM("joinRoom"),
    NEW_MESSAGE("newMessage"),
    ROOM_MESSAGES("roomMessages")
}

private const val URL = "http://10.0.2.2:3000" // "http://10.0.2.2:3000" is the network your Android emulator must use to join the localhost network on your computer
// "http://localhost:3000/" will not work

 object SocketHandler {

    private lateinit var webSocket: Socket

    @Synchronized
    fun setSocket() {
        val opts = IO.Options()
        opts.path = "/messages"
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
        if(webSocket.connected()){
            return
        }
        webSocket.connect()
    }

    @Synchronized
    fun joinRoom(roomID: String, userName: String) {
        webSocket.emit(EventType.USER_NAME.event, userName)
        webSocket.emit(EventType.JOIN_ROOM.event, roomID)
    }

    fun on(event: EventType, callback: Emitter.Listener) {
        try {
            webSocket.on(event.event, callback)
        } catch (e: Exception){}
    }


    @Synchronized
    fun sendMessage(content: String ) {
        webSocket.emit(EventType.NEW_MESSAGE.event, content)
    }

    @Synchronized
    fun closeConnection() {
        webSocket.disconnect()
    }
}
