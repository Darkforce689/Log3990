package com.example.polyscrabbleclient.message.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyscrabbleclient.message.domain.ConversationsManager
import com.example.polyscrabbleclient.ui.theme.create_already_exist
import com.example.polyscrabbleclient.ui.theme.create_name_forbiden
import kotlinx.coroutines.launch

enum class ErrorState {
    Error,
    NotError
}

enum class ConvoCreationError(val error: String) {
    AlreadyExisting("CONVERSATION_ALREADY_EXIST"),
    ConversationCreationForbiden("CONVERSATION_NAME_FORBIDEN"),
}

class CreateConversationViewModel: ViewModel() {
    val convoName: MutableState<String> = mutableStateOf("")
    val error: MutableState<String?> = mutableStateOf(null)

    fun onNameChange(name: String) {
        if (error.value != null) {
            error.value = null
        }
        convoName.value = name
    }

    fun createConversation(callback: (ErrorState) -> Unit) {
        if (!canCreateConversation()) {
            return
        }

        viewModelScope.launch {
            val conversationName = convoName.value
            ConversationsManager.createConversation(conversationName) {
                if (it === null || it.isEmpty()) {
                    callback(ErrorState.NotError)
                    afterCreate()
                    return@createConversation
                }
                error.value = parseError(it.first())
                callback(ErrorState.Error)
            }
        }
    }

    private fun afterCreate() {
        convoName.value = ""
        error.value = null
    }

    private fun parseError(rawError: String): String? {
        return when(rawError) {
            ConvoCreationError.AlreadyExisting.error -> create_already_exist
            ConvoCreationError.ConversationCreationForbiden.error -> create_name_forbiden
            else -> null
        }
    }

    fun canCreateConversation(): Boolean {
        return convoName.value.isNotEmpty()
    }

    fun cancel() {
        convoName.value = ""
        error.value = null
    }
}
