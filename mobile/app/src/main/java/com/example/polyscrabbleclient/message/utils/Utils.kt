package com.example.polyscrabbleclient.message.utils

import com.example.polyscrabbleclient.message.GAME_CONVERSATION_NAME
import com.example.polyscrabbleclient.message.GENERAL_CONVERSATION_NAME
import com.example.polyscrabbleclient.message.model.Conversation

fun isGameConversation(conversation: Conversation): Boolean {
    return conversation.name == GAME_CONVERSATION_NAME
}

fun conversationRoomId(conversation: Conversation): String {
    return if (isGameConversation(conversation)) {
        conversation._id
    } else {
        conversation.name
    }
}

fun isConversationLeavable(conversation: Conversation): Boolean {
    return conversation.name != GENERAL_CONVERSATION_NAME && conversation.name != GAME_CONVERSATION_NAME
}
