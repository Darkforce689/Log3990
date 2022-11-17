package com.example.polyscrabbleclient.message.model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

enum class MessageType {SYSTEM, ME, OTHER, ERROR }
data class Message(val content: String, val from: String, val type: MessageType, val date: ZonedDateTime?, val conversation: String?)
data class MessageDTO(val content: String, val from: String,  val date: String?, val conversation: String)
data class SystemMessage(val content: String, val date: String?, val conversation: String)


fun messageDTOToMessage(messageDTO: MessageDTO, type: MessageType, userName: String): Message {
    val date = parseMessageDate(messageDTO.date)
    return Message(messageDTO.content, userName, type, date, messageDTO.conversation)
}

fun parseMessageDate(date: String?): ZonedDateTime? {
    fun parseDate(dateStr: String): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX")
        return ZonedDateTime.parse(dateStr.replace('T',' '), formatter)
            .withZoneSameInstant(TimeZone.getDefault().toZoneId())
    }
    return if (date !== null) parseDate(date) else null
}
