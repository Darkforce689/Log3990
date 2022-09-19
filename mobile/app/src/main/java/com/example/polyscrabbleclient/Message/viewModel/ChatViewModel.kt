package com.example.polyscrabbleclient.message.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.message.EventType
import com.example.polyscrabbleclient.message.SocketHandler
import com.example.polyscrabbleclient.message.model.*
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class ChatBoxViewModel : ViewModel() {
    private val _messages: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>(listOf())
    var messages:LiveData<List<Message>> = _messages


    fun sendMessage(message: Message) {
        SocketHandler.sendMessage(message.content)
        addMessage(message)
    }

    var onNewMessage = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val content: String
        val from: String
        try {
            content = data.getString("content")
            from = data.getString("from")
            val type = if (from == User.name) MessageType.ME else MessageType.OTHER
            addMessage(content, from, type)
        } catch (e: JSONException) {
        }
    }

    private fun addMessage(content: String, from: String, type: MessageType) {
        val message = Message(content, from, type)
        val newList = _messages.value?.plus(message)
        _messages.postValue(newList)
    }
    private fun addMessage(message : Message) {
        val newList = _messages.value?.plus(message)
        _messages.postValue(newList)

    }

    fun joinRoom(roomId: String, user:User) {
        SocketHandler.socketConnection()
        SocketHandler.joinRoom(roomId, userName = user.name)
        SocketHandler.on(EventType.ROOM_MESSAGES, onNewMessage)
    }
}
