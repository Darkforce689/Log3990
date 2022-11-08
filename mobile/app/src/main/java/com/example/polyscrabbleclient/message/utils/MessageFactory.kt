package com.example.polyscrabbleclient.message.utils

import com.example.polyscrabbleclient.message.model.Message
import com.example.polyscrabbleclient.message.model.MessageDTO
import com.example.polyscrabbleclient.message.model.MessageType
import com.example.polyscrabbleclient.message.model.messageDTOToMessage
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
                val type = if (messageDTO.from == User._id) MessageType.ME else MessageType.OTHER
                messageDTOToMessage(messageDTO, type, user.name)
            }
            callback(messages)
        }
    }
}


