package com.example.polyscrabbleclient.message.domain

import androidx.compose.runtime.mutableStateListOf
import com.example.polyscrabbleclient.message.ChatSocketHandler
import com.example.polyscrabbleclient.message.GAME_CONVERSATION_NAME
import com.example.polyscrabbleclient.message.GENERAL_CONVERSATION_NAME
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.sources.ConversationRepository
import com.example.polyscrabbleclient.message.utils.conversationRoomId
import com.example.polyscrabbleclient.message.utils.isGameConversation

object ConversationsManager {
    val conversations = mutableStateListOf<Conversation>()
    private val conversationIds: MutableSet<String> = HashSet()
    private var gameConversation: Conversation? = null

    fun actualizeConversations(callback: () -> Unit) {
        val task = ConversationRepository.getJoinedConversations {
            val arranged = arrangeConversations(it)
            if (conversations.size > 0) {
                conversations.clear()
                conversationIds.clear()
            }
            conversations.addAll(arranged)
            conversationIds.addAll(arranged.map { it._id })

            val roomIds = conversations.map { conversation -> conversationRoomId(conversation) }
            ChatSocketHandler.setJoinedRooms(roomIds)
            callback()
        }
        task.start()
        task.join()
    }

    fun leaveConversation(conversationId: String, callback: () -> Unit) {
        val task = ConversationRepository.leaveConversation(conversationId) {
            // TODO maybe implement soft refresh
            actualizeConversations {
                callback()
            }
        }
        task.start()
        task.join()

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

    fun arrangeConversations(conversations: List<Conversation>): List<Conversation> {
        val gameConvo = gameConversation
        val generalConvo = conversations.findLast {
            it.name == GENERAL_CONVERSATION_NAME
        }

        val otherConvo = conversations.filter {
            it.name != GENERAL_CONVERSATION_NAME
        }

        val arranged = ArrayList<Conversation>()
        if (gameConvo != null) {
            arranged.add(gameConvo)
        }

        if (generalConvo != null) {
            arranged.add(generalConvo)
        }

        arranged.addAll(otherConvo)
        return arranged
    }

    fun joinGameConversation(gameToken: String) {
        gameConversation = Conversation(_id = gameToken, name = GAME_CONVERSATION_NAME)
        actualizeConversations() {
            // GAME CONVO ALWAYS FIRST IN arrangeConversations
            setCurrentConversationIndex(0)
        }
    }

    fun leaveGameConversation() {
        gameConversation = null
        actualizeConversations() {
             // MAYBE improve this behavior but still good UX
            setCurrentConversationIndex(0)
        }
    }
}
