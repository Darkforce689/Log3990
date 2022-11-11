package com.example.polyscrabbleclient.message.domain

import androidx.compose.runtime.mutableStateListOf
import com.example.polyscrabbleclient.message.ChatSocketHandler
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.sources.ConversationSource

object ConversationsManager {
    val conversations = mutableStateListOf<Conversation>()

    fun actualizeConversations(callback: () -> Unit) {
        println("actualize conversation")
        val task = ConversationSource.getJoinedConversations {
            if (conversations.size > 0) {
                conversations.clear()
            }
            conversations.addAll(it)
            val roomIds = conversations.map { conversation -> conversation.name }
            ChatSocketHandler.setJoinedRooms(roomIds)
            callback()
        }
        task.start()
        task.join()
    }

    fun leaveConversation(conversationId: String, callback: () -> Unit) {
        val task = ConversationSource.leaveConversation(conversationId) {
            // TODO maybe implement soft refresh
            actualizeConversations {}
        }
        task.start()
        task.join()
        callback()
    }

    fun createConversation(conversationName: String, callback: (List<String>?) -> Unit) {
        println("creating convo with name " + conversationName)
        val task = ConversationSource.createConversation(conversationName) {
            if (it.errors === null || it.errors.size == 0) {
                val conversationId = it.conversation._id
                joinConversation(conversationId) {
                    // TODO maybe implement soft refresh
                    actualizeConversations {
                        setCurrentConversation(conversationId)
                    }
                }
            }
            callback(it.errors)
        }
        task.start()
        task.join()
    }

    private val currentConvoSubs: MutableList<(Int) -> Unit> = ArrayList()
    private fun setCurrentConversation(conversationId: String) {
        val index = conversations.indexOfFirst {
            conversationId == it._id
        }
        currentConvoSubs.forEach {
            it(index)
        }
    }

    fun observeCurrentConvo(callback: (Int) -> Unit) {
        currentConvoSubs.add(callback)
    }

    fun resetObsCurrentConvo() {
        currentConvoSubs.clear()
    }
    
    fun joinConversation(conversationId: String, callback: () -> Unit) {
        val task = ConversationSource.joinConversation(conversationId) {
            callback()
        }
        task.start()
        task.join()
    }

    fun joinGameConversation(gameToken: String) {

    }
}
