package com.example.polyscrabbleclient.message.domain

import androidx.compose.runtime.mutableStateListOf
import com.example.polyscrabbleclient.message.ChatSocketHandler
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.sources.ConversationRepository

object ConversationsManager {
    val conversations = mutableStateListOf<Conversation>()
    private val conversationIds: MutableSet<String> = HashSet();

    fun actualizeConversations(callback: () -> Unit) {
        val task = ConversationRepository.getJoinedConversations {
            if (conversations.size > 0) {
                conversations.clear()
                conversationIds.clear()
            }
            conversations.addAll(it)
            conversationIds.addAll(conversations.map { it._id })

            val roomIds = conversations.map { conversation -> conversation.name }
            ChatSocketHandler.setJoinedRooms(roomIds)
            callback()
        }
        task.start()
        task.join()
    }

    fun leaveConversation(conversationId: String, callback: () -> Unit) {
        val task = ConversationRepository.leaveConversation(conversationId) {
            // TODO maybe implement soft refresh
            actualizeConversations {}
        }
        task.start()
        task.join()
        callback()
    }

    fun createConversation(conversationName: String, callback: (List<String>?) -> Unit) {
        val task = ConversationRepository.createConversation(conversationName) {
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
    fun setCurrentConversation(conversationId: String) {
        val index = conversations.indexOfFirst {
            conversationId == it._id
        }
        setCurrentConversationIndex(index)
    }

    fun setCurrentConversationWithName(name: String) {
        val index = conversations.indexOfFirst {
            name == it.name
        }
        setCurrentConversationIndex(index)
    }

    private fun setCurrentConversationIndex(index: Int) {
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
        val task = ConversationRepository.joinConversation(conversationId) {
            callback()
        }
        task.start()
        task.join()
    }

    fun isConversationJoined(conversation: Conversation): Boolean {
        return conversationIds.contains(conversation._id)
    }

    fun deleteConversation(
        conversation: Conversation,
        callback: () -> Unit,
    ) {
        val task = ConversationRepository.deleteConversation(conversation._id) {
            // TODO maybe improve current convo set to general after deletion
            setCurrentConversationWithName("general")
            callback()
        }
        task.start()
        task.join()
    }

    fun joinGameConversation(gameToken: String) {

    }
}
