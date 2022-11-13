package com.example.polyscrabbleclient.message.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.polyscrabbleclient.message.domain.ConversationsManager
import com.example.polyscrabbleclient.message.model.Conversation
import com.example.polyscrabbleclient.message.sources.ConversationRepository

class DeleteConversationViewModel: ViewModel() {
    val isConvoDeleting = mutableStateOf(false)

    val conversations: SnapshotStateList<Conversation> = mutableStateListOf()

    init {
        fetchCreatedConversations() {}
    }

    fun deleteConversation(conversation: Conversation) {
        if (isConvoDeleting.value) {
            return
        }

        isConvoDeleting.value = true

        Thread {
            ConversationsManager.deleteConversation(conversation) {
                ConversationsManager.actualizeConversations {}
                fetchCreatedConversations() {
                    isConvoDeleting.value = false
                }
            }
        }.start()
    }


    fun fetchCreatedConversations(callback: () -> Unit) {
        Thread {
            val task = ConversationRepository.getCreatedConversation() {
                if (conversations.size > 0) {
                    conversations.clear()
                }
                conversations.addAll(it)
                callback()
            }
            task.start()
            task.join()
        }.start()
    }
}
