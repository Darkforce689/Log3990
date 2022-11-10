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
        }
        task.start()
        task.join()
        val roomIds = conversations.map { conversation ->  conversation.name }
        ChatSocketHandler.setJoinedRooms(roomIds)
        callback()
    }

    fun joinGameConversation(gameToken: String) {

    }
}
