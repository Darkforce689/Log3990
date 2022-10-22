package com.example.polyscrabbleclient.message.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.message.EventType
import com.example.polyscrabbleclient.message.SocketHandler
import com.example.polyscrabbleclient.message.model.*
import com.example.polyscrabbleclient.user.User
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ChatBoxViewModel : ViewModel() {
    private var _messages: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>(listOf())
    var messages:LiveData<List<Message>> = _messages
    private var isInRoom : Boolean = false

    fun sendMessage(message: Message) {
        SocketHandler.sendMessage(message.content)
        addMessage(message)
    }

    fun reset() {
        _messages.value = emptyList()
        isInRoom = false
    }

    private var onNewMessage = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val content: String
        val from: String
        val date : ZonedDateTime
        try {
            content = data.getString("content")
            from = data.getString("from")
            val dateString = data.getString("date")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX")
            date = ZonedDateTime.parse(dateString.replace('T',' '), formatter)
                .withZoneSameInstant(TimeZone.getDefault().toZoneId())

            val type = if (from == User.name) MessageType.ME else MessageType.OTHER
            addMessage(content, from, date, type)
        } catch (e: JSONException) {
        }
    }

    private fun addMessage(content: String, from: String, date:ZonedDateTime, type: MessageType) {
        val message = Message(content, from, type, date)
        val newList = _messages.value?.plus(message)
        _messages.postValue(newList)
    }
    private fun addMessage(message : Message) {
        val newList = _messages.value?.plus(message)
        _messages.postValue(newList)

    }

    fun joinRoom(roomId: String) {
        if(isInRoom) {
            return
        }
        isInRoom = true
        val connectThread = Thread {
            SocketHandler.setSocket()
            SocketHandler.socketConnection()
        }
        connectThread.start()
        connectThread.join()
        SocketHandler.joinRoom(roomId)
        SocketHandler.on(EventType.ROOM_MESSAGES, onNewMessage)
    }
}
