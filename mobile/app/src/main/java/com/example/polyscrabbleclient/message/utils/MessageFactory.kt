package com.example.polyscrabbleclient.message.utils

import com.example.polyscrabbleclient.message.SYSTEM_ERROR_USERNAME
import com.example.polyscrabbleclient.message.SYSTEM_USERNAME
import com.example.polyscrabbleclient.message.model.*
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.user.UserRepository
import com.example.polyscrabbleclient.user.model.UserDTO

object MessageFactory {
    fun createMessage(messageDTO: MessageDTO, callback: (Message) -> Unit) {
        fun genMessageWithUser(user: UserDTO) {
            val type = if (messageDTO.from == User._id) MessageType.ME else MessageType.OTHER
            val message = messageDTOToMessage(messageDTO, type, user.name)
            callback(message)
        }
        val userId = messageDTO.from
        UserRepository.getUser(userId) { user -> genMessageWithUser(user) }
    }

    fun createMessages(messageDTOs: ArrayList<MessageDTO>, callback: (List<Message>) -> Unit) {
        val userIds = messageDTOs.map { messageDTO -> messageDTO.from }
        UserRepository.getUsers(userIds) { users ->
            val messages = messageDTOs.mapIndexed { index, messageDTO ->
                val user = users[index]
                // TODO implement system messages
                val type = getMessageType(user)
                messageDTOToMessage(messageDTO, type, user.name)
            }
            callback(messages)
        }
    }

    fun createSystemMessage(systemMessage: SystemMessage): Message {
        val date = parseMessageDate(systemMessage.date)
        return Message(
            content = systemMessage.content,
            from = SYSTEM_USERNAME,
            date = date,
            type = MessageType.SYSTEM,
            conversation = systemMessage.conversation,
        )
    }

    fun createSystemError(errorContent: String): Message {
        return Message(
            content = errorContent,
            from = SYSTEM_ERROR_USERNAME,
            date = null,
            type = MessageType.ERROR,
            conversation = null,
        )
    }

    private fun getMessageType(user: UserDTO): MessageType {
        if (user._id == User._id) {
            return MessageType.ME
        }

        if (user.name == SYSTEM_USERNAME) {
            return MessageType.SYSTEM
        }

        if (user.name == SYSTEM_ERROR_USERNAME) {
            return MessageType.ERROR
        }

        return MessageType.OTHER
    }
}


