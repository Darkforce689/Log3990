package com.example.polyscrabbleclient.message.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.polyscrabbleclient.message.domain.ConversationsManager
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.sources.ConversationSearchSource

class JoinConversationViewModel: ViewModel() {
    val conversationName = mutableStateOf("")

    val conversationsPager = Pager(PagingConfig(pageSize = 10)) {
        createConversationsSource()
    }.flow.cachedIn(viewModelScope)

    fun createConversationsSource(): ConversationSearchSource {
        return ConversationSearchSource(conversationName.value)
    }

    fun cancel() {
        conversationName.value = ""
    }

    fun joinConversation(conversation: Conversation, callback: () -> Unit) {
        val conversationId = conversation._id
        val t = Thread {
            ConversationsManager.joinConversation(conversationId) {
                ConversationsManager.actualizeConversations {
                    ConversationsManager.setCurrentConversation(conversationId)
                }
                callback()
            }
        }
        t.start()
    }

    fun isConversationJoinable(conversation: Conversation): Boolean {
        return !ConversationsManager.isConversationJoined(conversation)
    }
}
