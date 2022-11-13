package com.example.polyscrabbleclient.message.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.polyscrabbleclient.message.ChatSocketHandler
import com.example.polyscrabbleclient.message.EventType
import com.example.polyscrabbleclient.message.domain.ConversationsManager
import com.example.polyscrabbleclient.message.model.BaseMessage
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.model.Message
import com.example.polyscrabbleclient.message.model.MessageDTO
import com.example.polyscrabbleclient.message.sources.MessageSource
import com.example.polyscrabbleclient.message.utils.MessageFactory
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.Closeable


class ChatBoxViewModel : ViewModel() {
    var currentConversation: Conversation? = null
    var currentConvoId: MutableState<String?> = mutableStateOf(null) // needed to refresh list
    var selectedConvoIndex: MutableState<Int> = mutableStateOf(0)

    val conversations = ConversationsManager.conversations
    val currentConvoObs = { index: Int ->
        onSelectedConvo(index)
    }

    private var historySource = MessageSource(null)

    private var onNewMessage = Emitter.Listener { args ->
        val currentConvo = currentConversation
        if (currentConvo === null) {
            return@Listener
        }
        val data = args[0].toString()
        try {
            val messageDTO = Gson().fromJson(data, MessageDTO::class.java)
            println("message received: " + messageDTO.conversation)
            println("current convo" + currentConvo.name)
            if (messageDTO.conversation != currentConvo.name) { // TODO change when implementing game convo
                println("discarding message")
                return@Listener
            }
            MessageFactory.createMessage(messageDTO) { message -> addMessage(message) }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    init {
        actualizeConversations()
        connectSocket()
        ConversationsManager.observeCurrentConvo(currentConvoObs)
    }

    val messages = mutableStateListOf<Message>()
    var historyPager = Pager(PagingConfig(pageSize = 50), initialKey = 0) {
        createNewMessageSource()
    }.flow.cachedIn(viewModelScope)

    fun sendMessage(content: String) {
        if (currentConversation == null) {
            throw RuntimeException("You need to be in a conversation before sending a message")
        }
        println("current convo for new message" + currentConversation)
        val baseMessage =
            BaseMessage(content, currentConversation!!.name) // todo change here for game chat
        ChatSocketHandler.sendMessage(baseMessage)
    }

    fun reset() {
        messages.clear()
        ConversationsManager.resetObsCurrentConvo()
    }

    private fun connectSocket() {
        val connectThread = Thread {
            ChatSocketHandler.setSocket()
            ChatSocketHandler.socketConnection()
            ChatSocketHandler.on(EventType.ROOM_MESSAGES, onNewMessage)
        }
        connectThread.start()
        connectThread.join()
    }

    fun actualizeConversations() {
        Thread {
            ConversationsManager.actualizeConversations() {
                setCurrentConvoAfterUpdate()
            }
        }.start()
    }

    fun leaveConversation(index: Int) {
        val conversation = conversations[index]
        Thread {
            ConversationsManager.leaveConversation(conversation._id) {
                setCurrentConvoAfterUpdate()
            }
        }.start()
    }

    private fun setCurrentConvoAfterUpdate() {
        if (conversations.size == 0) {
            return
        }
        val currentConvo = currentConversation

        // default first convo
        if (currentConvo === null) {
            onSelectedConvo(0)
            return
        }

        val convoIndex = conversations.indexOf(currentConvo)
        if (convoIndex == -1) {
            onSelectedConvo(0)
            return
        }
        onSelectedConvo(convoIndex)
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

    fun onSelectedConvo(index: Int) {
        println("change convo hashcode " + this.hashCode())
        selectedConvoIndex.value = index
        val selectedConvo = conversations[index]
        changeCurrentConversation(selectedConvo)
        currentConvoId.value = selectedConvo._id // todo change for game convo
    }

    private fun changeCurrentConversation(conversation: Conversation) {
        currentConversation = conversation
        messages.clear()
        println("changing convo" + currentConversation)
    }

    private fun createNewMessageSource(): MessageSource {
        val conversationId = if (currentConversation === null) null else currentConversation!!._id
        return MessageSource(conversationId)
    }
}
