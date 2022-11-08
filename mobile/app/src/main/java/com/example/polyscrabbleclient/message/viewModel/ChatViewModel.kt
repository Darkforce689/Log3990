package com.example.polyscrabbleclient.message.viewModel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.message.ChatSocketHandler
import com.example.polyscrabbleclient.message.EventType
import com.example.polyscrabbleclient.message.N_MESSAGE_TO_FETCH
import com.example.polyscrabbleclient.message.model.*
import com.example.polyscrabbleclient.message.sources.MessageRepository
import com.example.polyscrabbleclient.message.sources.MessageSource
import com.example.polyscrabbleclient.message.utils.MessageFactory
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.net.URL


class ChatBoxViewModel : ViewModel() {
    var currentConversation: MutableState<Conversation?> = mutableStateOf(Conversation("634a17afffd19bae95f4eba8" ,"general"))
    private val historySource = MessageSource()

    init {
        if (currentConversation.value !== null) {
            historySource.setCurrentConversation(currentConversation.value!!._id)
        }
    }

    val messages = mutableStateListOf<Message>()
    val historyPager = Pager(PagingConfig(pageSize = 50)) {
        historySource
    }.flow.cachedIn(viewModelScope)

    private var isInRoom : Boolean = false

    fun sendMessage(content: String) {
        if (currentConversation.value == null) {
            throw RuntimeException("You need to be in a conversation before sending a message")
        }
        val baseMessage = BaseMessage(content, currentConversation.value!!.name)
        ChatSocketHandler.sendMessage(baseMessage)
    }

    fun reset() {
        messages.clear()
        isInRoom = false
    }

    private var onNewMessage = Emitter.Listener { args ->
        val data = args[0].toString()
        try {
            val messageDTO = Gson().fromJson(data, MessageDTO::class.java)
            MessageFactory.createMessage(messageDTO) { message -> addMessage(message) }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun addMessage(newMessage: Message) {
        fun findMessageInsertionIndex(): Int {
            var insertIndex = messages.size
            var skips = 1
            for (message in messages.reversed()) {
                if (message.date === null) {
                    skips++
                    continue
                }
                if (message.date <= newMessage.date) {
                    break
                }
                insertIndex -= skips
                skips = 1
            }
            return insertIndex
        }
        // This is worst case O(n) but will be O(1) 99.9999% of the time. This is used as an Insurance
        val insertionIndex = findMessageInsertionIndex()
        messages.add(insertionIndex, newMessage)
        historySource.offset++;
    }

    private var isFirstMessageLoaded = false;
    fun setCurrentConversation() {
        if (isFirstMessageLoaded) {
            return
        }
        isFirstMessageLoaded = true
    }


    fun joinRoom(roomId: String) {
        if(isInRoom) {
            return
        }
        isInRoom = true
        val connectThread = Thread {
            ChatSocketHandler.setSocket()
            ChatSocketHandler.socketConnection()
        }
        connectThread.start()
        connectThread.join()
        ChatSocketHandler.joinRoom(roomId)
        ChatSocketHandler.on(EventType.ROOM_MESSAGES, onNewMessage)
    }
}
